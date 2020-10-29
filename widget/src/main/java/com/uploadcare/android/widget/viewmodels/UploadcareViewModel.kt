package com.uploadcare.android.widget.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFileCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.FileUploader
import com.uploadcare.android.library.upload.Uploader
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.FileType
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.data.SocialSourcesResponse
import com.uploadcare.android.widget.utils.SingleLiveEvent
import com.uploadcare.android.widget.worker.FileUploadWorker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class UploadcareViewModel(application: Application) : AndroidViewModel(application) {

    val progressDialogCommand = SingleLiveEvent<ProgressData>()
    val closeWidgetCommand = SingleLiveEvent<UploadcareApiException?>()
    val showSocialSourcesCommand = SingleLiveEvent<Pair<List<SocialSource>, FileType>>()
    val launchSocialSourceCommand = SingleLiveEvent<SocialData>()
    val launchCamera = SingleLiveEvent<Pair<Uri, MediaType>>()
    val launchFilePicker = SingleLiveEvent<FileType>()
    val uploadCompleteCommand = SingleLiveEvent<UploadcareFile>()
    val uploadingInBackgroundCommand = SingleLiveEvent<UUID>()
    val uploadProgress = MutableLiveData<Int>().apply { value = 0 }

    private var network: String? = null
    private var storeUponUpload = false
    private var sources: List<SocialSource>? = null
    private var fileType = FileType.any
    private var tempFileUri: Uri? = null
    private var signature: String? = null
    private var expire: String? = null
    private var cancelable : Boolean = false
    private var showProgress : Boolean = false
    private var backgroundUpload : Boolean = false
    private var uploader: Uploader? = null

    fun start(bundle: Bundle) {
        network = bundle.getString("network")
        storeUponUpload = bundle.getBoolean("store", false)
        fileType = bundle.getString("fileType")?.let {
            FileType.valueOf(it)
        } ?: FileType.any
        signature = bundle.getString("signature")
        expire = bundle.getString("expire")
        cancelable = bundle.getBoolean("cancelable", false)
        showProgress = bundle.getBoolean("showProgress", false)
        backgroundUpload = bundle.getBoolean("backgroundUpload", false)
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

        if (uri == null) {
            closeWidgetCommand.call()
            return
        }

        if (backgroundUpload) {
            val requestBuilder = OneTimeWorkRequestBuilder<FileUploadWorker>()
            requestBuilder.addTag(FileUploadWorker.TAG)
            requestBuilder.setInputData(workDataOf(
                    FileUploadWorker.KEY_FILE_URI to uri.toString(),
                    FileUploadWorker.KEY_CANCELABLE to cancelable,
                    FileUploadWorker.KEY_SHOW_PROGRESS to showProgress,
                    FileUploadWorker.KEY_STORE to storeUponUpload,
                    FileUploadWorker.KEY_SIGNATURE to signature,
                    FileUploadWorker.KEY_EXPIRE to expire,
            ))
            val uploadWorkRequest: WorkRequest = requestBuilder.build()
            WorkManager.getInstance(getContext()).enqueue(uploadWorkRequest)
            uploadingInBackgroundCommand.postValue(uploadWorkRequest.id)
        } else {
            progressDialogCommand.postValue(ProgressData(
                    true,
                    getContext().getString(R.string.ucw_action_loading_image),
                    cancelable,
                    showProgress))

            uploader = FileUploader(UploadcareWidget.getInstance(getContext()).uploadcareClient,
                    uri, getContext())
                    .store(storeUponUpload)
                    .signedUpload(signature ?: "", expire ?: "")

            uploader!!.uploadAsync(object : UploadFileCallback {
                override fun onFailure(e: UploadcareApiException) {
                    progressDialogCommand.postValue(ProgressData(false))
                    closeWidgetCommand.postValue(e)
                    uploader = null
                }

                override fun onProgressUpdate(
                        bytesWritten: Long,
                        contentLength: Long,
                        progress: Double) {
                    if (showProgress) {
                        uploadProgress.value = (progress * 100).roundToInt()
                    }
                }

                override fun onSuccess(result: UploadcareFile) {
                    progressDialogCommand.postValue(ProgressData(false))
                    uploadCompleteCommand.postValue(result)
                    uploader = null
                }
            })
        }
    }

    fun canlcelUpload(){
        uploader?.cancel()
        uploader = null
        closeWidgetCommand.postValue(UploadcareApiException("Canceled"))
    }

    private fun getAvailableNetworks() {
        progressDialogCommand.postValue(ProgressData(true,
                getContext().getString(R.string.ucw_action_loading_networks)))
        UploadcareWidget.getInstance(getApplication())
                .socialApi
                .getSources()
                .enqueue(object : Callback<SocialSourcesResponse> {
                    override fun onFailure(call: Call<SocialSourcesResponse>, t: Throwable) {
                        progressDialogCommand.postValue(ProgressData(false))
                        closeWidgetCommand.postValue(UploadcareApiException(t))
                    }

                    override fun onResponse(call: Call<SocialSourcesResponse>,
                                            response: Response<SocialSourcesResponse>) {
                        progressDialogCommand.postValue(ProgressData(false))
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
                launchSocialSourceCommand.postValue(SocialData(
                        socialSource,
                        storeUponUpload,
                        cancelable,
                        showProgress,
                        backgroundUpload))
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
                launchSocialSourceCommand.postValue(SocialData(
                        socialSource,
                        storeUponUpload,
                        cancelable,
                        showProgress,
                        backgroundUpload))
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

data class ProgressData(
        val show: Boolean,
        val message: String? = null,
        val cancelable: Boolean = false,
        val showProgress: Boolean = false)

data class SocialData(
        val socialSource: SocialSource,
        val storeUponUpload:Boolean = false,
        val cancelable: Boolean = false,
        val showProgress: Boolean = false,
        val backgroundUpload: Boolean = false
)