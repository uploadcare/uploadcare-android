package com.uploadcare.android.widget.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.FileUploader
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.data.SocialSourcesResponse
import com.uploadcare.android.widget.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UploadcareViewModel(application: Application) : AndroidViewModel(application) {

    val progressDialogCommand = SingleLiveEvent<Pair<Boolean, String?>>()
    val closeWidgetCommand = SingleLiveEvent<UploadcareApiException?>()
    val showSocialSourcesCommand = SingleLiveEvent<Pair<List<SocialSource>, FileType>>()
    val launchSocialSourceCommand = SingleLiveEvent<Pair<SocialSource, Boolean>>()
    val launchCamera = SingleLiveEvent<Pair<Uri, MediaType>>()
    val launchFilePicker = SingleLiveEvent<FileType>()
    val uploadCompleteCommand = SingleLiveEvent<UploadcareFile>()

    private var network: String? = null
    private var storeUponUpload = false
    private var sources: List<SocialSource>? = null
    private var fileType = FileType.any
    private var tempFileUri: Uri? = null

    fun start(bundle: Bundle) {
        network = bundle.getString("network")
        storeUponUpload = bundle.getBoolean("store", false)
        fileType = bundle.getString("fileType")?.let {
            FileType.valueOf(it)
        } ?: FileType.any

        if (isLocalNetwork(network)) {
            launchLocalNetwork(network)
        } else {
            sources?.let {
                showNetworks(it)
            } ?: getAvailableNetworks()
        }
    }

    fun onRestoreInstanceState(bundle: Bundle) {
        sources = bundle
                .getParcelableArrayList("${UploadcareViewModel::class.simpleName}_socialSource")
        bundle.getString("${UploadcareViewModel::class.simpleName}_tempFileUri")?.let {
            tempFileUri = Uri.parse(it)
        }
    }

    fun onSaveInstanceState(bundle: Bundle) {
        sources?.let {
            bundle.putParcelableArrayList("${UploadcareViewModel::class.simpleName}_socialSource",
                    ArrayList(it))
        }
        tempFileUri?.let {
            bundle.putString("${UploadcareViewModel::class.simpleName}_tempFileUri",
                    tempFileUri.toString())
        }
    }

    fun sourceSelected(socialSource: SocialSource) {
        launchNetwork(socialSource)
    }

    fun uploadFile(fileUri: Uri? = null) {
        val uri = fileUri ?: tempFileUri
        uri?.let { uri ->
            progressDialogCommand.postValue(Pair(true,
                    getContext().getString(R.string.ucw_action_loading_image)))

            val uploader = FileUploader(UploadcareWidget.getInstance(getContext()).uploadcareClient,
                    uri, getContext()).store(storeUponUpload)
            uploader.uploadAsync(object : UploadcareFileCallback {
                override fun onFailure(e: UploadcareApiException) {
                    progressDialogCommand.postValue(Pair(false, null))
                    closeWidgetCommand.postValue(e)
                }

                override fun onSuccess(result: UploadcareFile) {
                    progressDialogCommand.postValue(Pair(false, null))
                    uploadCompleteCommand.postValue(result)
                }
            })
        } ?: closeWidgetCommand.call()
    }

    private fun getAvailableNetworks() {
        progressDialogCommand.postValue(Pair(true,
                getContext().getString(R.string.ucw_action_loading_networks)))
        UploadcareWidget.getInstance(getApplication())
                .socialApi
                .getSources()
                .enqueue(object : Callback<SocialSourcesResponse> {
                    override fun onFailure(call: Call<SocialSourcesResponse>, t: Throwable) {
                        progressDialogCommand.postValue(Pair(false, null))
                        closeWidgetCommand.postValue(UploadcareApiException(t))
                    }

                    override fun onResponse(call: Call<SocialSourcesResponse>,
                                            response: Response<SocialSourcesResponse>) {
                        progressDialogCommand.postValue(Pair(false, null))
                        val data = response.body()
                        sources = data?.sources
                        sources?.let { showNetworks(it) } ?: closeWidgetCommand.call()
                    }
                })
    }

    private fun showNetworks(sources: List<SocialSource>) {
        if (network == null) {
            //Show network selector
            showSocialSourcesCommand.postValue(Pair(sources, fileType))
        } else {
            var socialSource: SocialSource? = null
            for (source in sources) {
                if (source.name.equals(network, true)) {
                    socialSource = source
                    break
                }
            }

            socialSource?.let {
                launchSocialSourceCommand.postValue(Pair(socialSource, storeUponUpload))
            } ?: closeWidgetCommand.call()
        }
    }

    private fun getContext(): Context {
        return getApplication()
    }

    private fun launchNetwork(socialSource: SocialSource) {
        when (socialSource.name) {
            SocialNetwork.SOCIAL_NETWORK_CAMERA.rawValue,
            SocialNetwork.SOCIAL_NETWORK_VIDEOCAM.rawValue,
            SocialNetwork.SOCIAL_NETWORK_FILE.rawValue -> {
                launchLocalNetwork(socialSource.name)
            }
            else -> {
                launchSocialSourceCommand.postValue(Pair(socialSource, storeUponUpload))
            }
        }
    }

    private fun launchLocalNetwork(network: String?) {
        when (network) {
            SocialNetwork.SOCIAL_NETWORK_CAMERA.rawValue -> {
                tempFileUri = getOutputMediaFileUri(MediaType.IMAGE)
                tempFileUri?.let {
                    launchCamera.postValue(Pair(it, MediaType.IMAGE))
                }
            }
            SocialNetwork.SOCIAL_NETWORK_VIDEOCAM.rawValue -> {
                tempFileUri = getOutputMediaFileUri(MediaType.VIDEO)
                tempFileUri?.let {
                    launchCamera.postValue(Pair(it, MediaType.VIDEO))
                }
            }
            SocialNetwork.SOCIAL_NETWORK_FILE.rawValue -> {
                launchFilePicker.postValue(fileType)
            }
            else -> {
                throw IllegalArgumentException("Unsupported SocialSource: $network")
            }
        }
    }

    private fun isLocalNetwork(network: String?): Boolean {
        network ?: return false

        return when (network) {
            SocialNetwork.SOCIAL_NETWORK_CAMERA.rawValue,
            SocialNetwork.SOCIAL_NETWORK_VIDEOCAM.rawValue,
            SocialNetwork.SOCIAL_NETWORK_FILE.rawValue -> {
                true
            }
            else -> false
        }
    }

    /**
     * Create a file Uri for saving an image or video
     */
    private fun getOutputMediaFileUri(mediaType: MediaType): Uri {
        return FileProvider.getUriForFile(getApplication(),
                getContext().packageName + ".fileprovider",
                getOutputMediaFile(mediaType))
    }

    /**
     *  Create a File for saving an image or video
     */
    private fun getOutputMediaFile(mediaType: MediaType): File {
        val mediaStorageDir = File(getContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Cache")
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return when (mediaType) {
            MediaType.IMAGE -> File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + ".jpg")
            else -> File(mediaStorageDir.path + File.separator + "VID_" + timeStamp + ".mp4")
        }
    }
}

enum class MediaType {
    IMAGE, VIDEO
}