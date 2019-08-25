package com.uploadcare.android.example.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import com.google.android.material.textfield.TextInputEditText
import com.uploadcare.android.example.R
import com.uploadcare.android.example.fragments.UploadFragment
import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.FileUploader
import com.uploadcare.android.library.upload.MultipleFilesUploader
import com.uploadcare.android.library.upload.UrlUploader
import com.uploadcare.android.widget.controller.SocialNetwork
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.controller.UploadcareWidgetResult
import com.uploadcare.android.widget.utils.SingleLiveEvent

class UploadViewModel(application: Application) : AndroidViewModel(application) {

    val url = ObservableField<String>()
    val urlError = ObservableField<String>()
    val loading = ObservableBoolean(false)
    val status = ObservableField<String>()

    val launchGetFilesCommand = SingleLiveEvent<Void>()
    val launchFilePickerCommand = SingleLiveEvent<Void>()

    /**
     * Initialize {@link UploadcareClient}
     */
    private val client = UploadcareWidget.getInstance(application).uploadcareClient

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

    fun uploadWidgetAny(fragment: UploadFragment) {
        UploadcareWidget.getInstance(getContext()).style = -1
        UploadcareWidget.getInstance(getContext()).selectFile(fragment, true)
    }

    fun uploadWidgetInstagram(fragment: UploadFragment) {
        UploadcareWidget.getInstance(getContext()).style = R.style.CustomUploadCareIndigoPink
        UploadcareWidget.getInstance(getContext())
                .selectFileFrom(fragment, SocialNetwork.SOCIAL_NETWORK_INSTAGRAM, true)
    }

    fun uploadWidgetFacebook(fragment: UploadFragment) {
        UploadcareWidget.getInstance(getContext()).style = R.style.CustomUploadCareGreenRed
        UploadcareWidget.getInstance(getContext())
                .selectFileFrom(fragment, SocialNetwork.SOCIAL_NETWORK_FACEBOOK, true)
    }

    fun uploadWidgetDropbox(fragment: UploadFragment) {
        UploadcareWidget.getInstance(getContext()).style = -1
        UploadcareWidget.getInstance(getContext())
                .selectFileFrom(fragment, SocialNetwork.SOCIAL_NETWORK_DROPBOX, true)
    }

    fun onUploadResult(result: UploadcareWidgetResult) {
        result.uploadcareFile?.let {
            showProgressOrResult(false, it.toString())
        } ?: showProgressOrResult(false, result.exception?.message.toString())
    }

    /**
     * Uploads file from {@link Uri} using UploadcareClient.
     *
     * @param fileUri file {@link Uri}
     */
    fun uploadFile(fileUri: Uri) {
        showProgressOrResult(true, getContext().getString(R.string.activity_main_status_uploading))
        val uploader = FileUploader(client, fileUri, getContext()).store(true)
        uploader.uploadAsync(object : UploadcareFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                showProgressOrResult(false, e.localizedMessage)
            }

            override fun onSuccess(result: UploadcareFile) {
                showProgressOrResult(false, result.toString())
            }
        })
    }

    /**
     * Uploads files from list of {@link Uri} using UploadcareClient.
     *
     * @param filesUriList list of files {@link Uri}
     */
    fun uploadFiles(filesUriList: List<Uri>) {
        showProgressOrResult(true, getContext().getString(R.string.activity_main_status_uploading))
        val uploader = MultipleFilesUploader(client, filesUriList, getContext()).store(true)
        uploader.uploadAsync(object : UploadFilesCallback {

            override fun onFailure(e: UploadcareApiException) {
                showProgressOrResult(false, e.localizedMessage)
            }

            override fun onSuccess(result: List<UploadcareFile>) {
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

    /**
     * Uploads file from url using UploadcareClient.
     *
     * @param client    [UploadcareClient]
     * @param sourceUrl url of the original file.
     */
    private fun uploadFromUrl(client: UploadcareClient, sourceUrl: String) {
        showProgressOrResult(true, getContext().getString(R.string.activity_main_status_uploading))
        val uploader = UrlUploader(client, sourceUrl).store(true)
        uploader.uploadAsync(object : UploadcareFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                showProgressOrResult(false, e.localizedMessage)
            }

            override fun onSuccess(result: UploadcareFile) {
                showProgressOrResult(false, result.toString())
            }
        })
    }

    /**
     * Checks if user entered valid url.
     *
     * @return `true` if entered url is valid, `false` otherwise.
     */
    private fun checkUrl(url: String?): Boolean {
        return if (url != null && url.isNotEmpty()) {
            urlError.set(null)
            true
        } else {
            urlError.set(getContext().getString(R.string.activity_main_hint_upload_url))
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
        loading.set(progress)
        status.set(message)
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