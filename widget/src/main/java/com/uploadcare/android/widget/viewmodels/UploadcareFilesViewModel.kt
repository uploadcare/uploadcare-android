package com.uploadcare.android.widget.viewmodels

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.upload.UrlUploader
import com.uploadcare.android.widget.R
import com.uploadcare.android.widget.controller.UploadcareWidget
import com.uploadcare.android.widget.data.SelectedFile
import com.uploadcare.android.widget.data.SocialSource
import com.uploadcare.android.widget.utils.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadcareFilesViewModel(application: Application) : AndroidViewModel(application) {

    val progressDialogCommand = SingleLiveEvent<Pair<Boolean, String?>>()
    val closeWidgetCommand = SingleLiveEvent<UploadcareApiException?>()
    val uploadCompleteCommand = SingleLiveEvent<UploadcareFile>()
    val showErrorCommand = SingleLiveEvent<String>()

    val isRoot = ObservableBoolean(true)

    private var socialSource: SocialSource? = null
    private var storeUponUpload = false

    fun start(source: SocialSource, store: Boolean) {
        socialSource = source
        storeUponUpload = store
    }

    fun selectFile(fileUrl: String) {
        socialSource?.let { source ->
            progressDialogCommand.postValue(Pair(true,
                    getContext().getString(R.string.ucw_action_loading_image)))

            UploadcareWidget.getInstance(getContext())
                    .socialApi
                    .selectFile(source.getCookie(getContext()), source.urls.done, fileUrl)
                    .enqueue(object : Callback<SelectedFile> {
                        override fun onFailure(call: Call<SelectedFile>, t: Throwable) {
                            error(UploadcareApiException(t))
                        }

                        override fun onResponse(call: Call<SelectedFile>,
                                                response: Response<SelectedFile>) {
                            response.body()?.let {
                                uploadFileFromUrl(it)
                            } ?: error()
                        }
                    })
        }
    }

    fun saveCookie(cookie: String) {
        socialSource?.let { it.saveCookie(getContext(), cookie) }
    }

    fun signOut() {
        socialSource?.let { source ->
            progressDialogCommand.postValue(Pair(true,
                    getContext().getString(R.string.ucw_action_signout)))

            UploadcareWidget.getInstance(getContext())
                    .socialApi
                    .signOut(source.getCookie(getContext()), source.urls.session)
                    .enqueue(object : Callback<Any> {
                        override fun onFailure(call: Call<Any>, t: Throwable) {
                            progressDialogCommand.postValue(Pair(false, null))
                            showErrorCommand.postValue(getContext()
                                    .getString(R.string.ucw_error_auth))
                        }

                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            progressDialogCommand.postValue(Pair(false, null))
                            source.deleteCookie(getContext())
                            closeWidgetCommand.postValue(null)
                        }
                    })
        }
    }

    private fun uploadFileFromUrl(file: SelectedFile) {
        val uploader = UrlUploader(UploadcareWidget
                .getInstance(getContext()).uploadcareClient, file.url)
                .store(storeUponUpload)
        uploader.uploadAsync(object : UploadcareFileCallback {
            override fun onFailure(e: UploadcareApiException) {
                error(e)
            }

            override fun onSuccess(result: UploadcareFile) {
                progressDialogCommand.postValue(Pair(false, null))
                uploadCompleteCommand.postValue(result)
            }

        })
    }

    private fun error(e: UploadcareApiException? = null) {
        progressDialogCommand.postValue(Pair(false, null))
        closeWidgetCommand.postValue(e)
    }

    private fun getContext(): Context {
        return getApplication()
    }
}