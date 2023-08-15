package com.uploadcare.android.example.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.android.material.textfield.TextInputEditText
import com.uploadcare.android.example.R
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFileCallback
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.*
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.controller.UploadcareWidgetResult
import com.uploadcare.android.widget.utils.SingleLiveEvent
import com.uploadcare.android.widget.controller.UploadcareWidgetParams
import java.util.*
import kotlin.math.roundToInt

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    val url = MutableLiveData<String>()
    val urlError = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>().apply { value = false }
    val allowUploadCancel = MutableLiveData<Boolean>().apply { value = false }
    val allowUploadPause = MutableLiveData<Boolean>().apply { value = false }
    val showUploadProgress = MutableLiveData<Boolean>().apply { value = false }
    val allowUploadCancelWidget = MutableLiveData<Boolean>().apply { value = false }
    val showUploadProgressWidget = MutableLiveData<Boolean>().apply { value = false }
    val backgroundUploadWidget = MutableLiveData<Boolean>().apply { value = false }
    val uploadPaused = MutableLiveData<Boolean>().apply { value = false }
    val uploadProgress = MutableLiveData<Int>().apply { value = 0 }
    val status = MutableLiveData<String>()

    private val backgroundUploadUUID = MutableLiveData<UUID>()
    val backgroundUploadResult: LiveData<UploadcareWidgetResult> =
        backgroundUploadUUID.switchMap { uuid ->
            UploadcareWidget
                .getInstance()
                .backgroundUploadResult(application.applicationContext, uuid)
        }

    val launchGetFilesCommand = SingleLiveEvent<Void>()
    val launchFilePickerCommand = SingleLiveEvent<Void>()

    private var uploader: Uploader? = null

    private var multipleUploader: MultipleUploader? = null

    /**
     * Initialize {@link UploadcareClient}
     */
    private val client = UploadcareWidget.getInstance().uploadcareClient

    fun launchGetFiles() {
        launchGetFilesCommand.call()
    }

    fun launchFilePicker() {
        launchFilePickerCommand.call()
    }

    fun uploadUrl(editText: TextInputEditText?, url: String?) {
        editText?.let { hideKeyboard(it) }
        if (checkUrl(url)) {
            url?.let { uploadFromUrl(client, it) }
        }
    }

    fun uploadWidgetAny(uploadcareLauncher: ActivityResultLauncher<UploadcareWidgetParams>) {
        uploadWidget(uploadcareLauncher)
    }

    fun uploadWidgetInstagram(uploadcareLauncher: ActivityResultLauncher<UploadcareWidgetParams>) {
        uploadWidget(
            uploadcareLauncher = uploadcareLauncher,
            network = SocialNetwork.SOCIAL_NETWORK_INSTAGRAM,
            style = R.style.CustomUploadCareIndigoPink
        )
    }

    fun uploadWidgetFacebook(uploadcareLauncher: ActivityResultLauncher<UploadcareWidgetParams>) {
        uploadWidget(
            uploadcareLauncher = uploadcareLauncher,
            network = SocialNetwork.SOCIAL_NETWORK_FACEBOOK,
            style = R.style.CustomUploadCareGreenRed
        )
    }

    fun uploadWidgetDropbox(uploadcareLauncher: ActivityResultLauncher<UploadcareWidgetParams>) {
        uploadWidget(uploadcareLauncher, SocialNetwork.SOCIAL_NETWORK_DROPBOX)
    }

    private fun uploadWidget(
        uploadcareLauncher: ActivityResultLauncher<UploadcareWidgetParams>,
        network: SocialNetwork? = null,
        style: Int = -1,
    ) {
        val params = UploadcareWidgetParams(
            style = style,
            network = network,
            cancelable = allowUploadCancelWidget.value ?: false,
            showProgress = showUploadProgressWidget.value ?: false,
            backgroundUpload = backgroundUploadWidget.value ?: false
        )

        uploadcareLauncher.launch(params)
    }

    fun onUploadResult(result: UploadcareWidgetResult) {
        if (result.isSuccess()) {
            if (result.uploadcareFile != null) {
                showProgressOrResult(false, result.uploadcareFile.toString())
            } else if (result.backgroundUploadUUID != null) {
                showProgressOrResult(
                    false,
                    "Uploading in background: ${result.backgroundUploadUUID.toString()}"
                )

                if (result.backgroundUploadUUID != backgroundUploadUUID.value) {
                    backgroundUploadUUID.value = result.backgroundUploadUUID!!
                }
            }
        } else {
            showProgressOrResult(false, result.exception?.message.toString())
        }
    }

    /**
     * Uploads files from list of {@link Uri} using UploadcareClient.
     *
     * @param filesUriList list of files {@link Uri}
     */
    fun uploadFiles(filesUriList: List<Uri>) {
        showProgressOrResult(true, getContext().getString(R.string.activity_main_status_uploading))
        allowUploadPause.value = true
        multipleUploader = MultipleFilesUploader(client, filesUriList, getContext())
            .store(true)
            .also { multipleFilesUploader ->
                multipleFilesUploader.uploadAsync(object : UploadFilesCallback {
                    override fun onFailure(e: UploadcareApiException) {
                        allowUploadPause.value = false
                        showProgressOrResult(false, e.message ?: "")
                    }

                    override fun onProgressUpdate(
                        bytesWritten: Long,
                        contentLength: Long,
                        progress: Double
                    ) {
                        if (showUploadProgress.value == true) {
                            uploadProgress.value = (progress * 100).roundToInt()
                        }
                    }

                    override fun onSuccess(result: List<UploadcareFile>) {
                        allowUploadPause.value = false
                        val resultStringBuilder = StringBuilder()
                        for (file in result) {
                            resultStringBuilder.append(file.toString())
                                .append(System.getProperty("line.separator"))
                                .append(System.getProperty("line.separator"))
                        }
                        showProgressOrResult(false, resultStringBuilder.toString())
                    }
                })
            }
    }

    fun cancelUpload() {
        uploader?.cancel()
        multipleUploader?.cancel()
        uploader = null
        multipleUploader = null
        showProgressOrResult(false, "Canceled")
    }

    fun tooglePause() {
        // pause/resume upload if supported.
        val fileUploader: FileUploader? = uploader as? FileUploader
        fileUploader?.let { uploader ->
            if (uploader.isPaused) {
                uploader.resume()
            } else {
                uploader.pause()
            }
            uploadPaused.value = uploader.isPaused
        }

        val multipleFilesUploader: MultipleFilesUploader? =
            multipleUploader as? MultipleFilesUploader
        multipleFilesUploader?.let {
            if (multipleFilesUploader.isPaused) {
                multipleFilesUploader.resume()
            } else {
                multipleFilesUploader.pause()
            }
            uploadPaused.value = multipleFilesUploader.isPaused
        }
    }

    /**
     * Uploads file from url using UploadcareClient.
     *
     * @param client    [UploadcareClient]
     * @param sourceUrl url of the original file.
     */
    private fun uploadFromUrl(client: UploadcareClient, sourceUrl: String) {
        showProgressOrResult(true, getContext().getString(R.string.activity_main_status_uploading))
        uploader = UrlUploader(client, sourceUrl)
            .store(true)
            .also { urlUploader ->
                urlUploader.uploadAsync(object : UploadFileCallback {
                    override fun onFailure(e: UploadcareApiException) {
                        showProgressOrResult(false, e.message ?: "")
                    }

                    override fun onProgressUpdate(
                        bytesWritten: Long,
                        contentLength: Long,
                        progress: Double
                    ) {
                        if (showUploadProgress.value == true) {
                            uploadProgress.value = (progress * 100).roundToInt()
                        }
                    }

                    override fun onSuccess(result: UploadcareFile) {
                        showProgressOrResult(false, result.toString())
                    }
                })
            }
    }

    /**
     * Checks if user entered valid url.
     *
     * @return `true` if entered url is valid, `false` otherwise.
     */
    private fun checkUrl(url: String?): Boolean {
        return if (url != null && url.isNotEmpty()) {
            urlError.value = ""
            true
        } else {
            urlError.value = getContext().getString(R.string.activity_main_hint_upload_url)
            false
        }
    }

    /**
     * Shows current status message.
     *
     * @param progress `true` if request is in progress.
     * @param message  message to show.
     */
    private fun showProgressOrResult(progress: Boolean, message: String) {
        if (progress) {
            uploadProgress.value = 0
        } else {
            uploader = null
            multipleUploader = null
        }
        loading.value = progress
        status.value = message
    }

    private fun getContext(): Context {
        return getApplication()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
