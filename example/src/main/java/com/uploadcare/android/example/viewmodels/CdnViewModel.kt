package com.uploadcare.android.example.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.uploadcare.android.example.R
import com.uploadcare.android.library.addon.AWSRekognitionAddOn
import com.uploadcare.android.library.addon.AWSRekognitionModerationAddOn
import com.uploadcare.android.library.addon.AddOnExecutor
import com.uploadcare.android.library.addon.ClamAVAddOn
import com.uploadcare.android.library.addon.RemoveBgAddOn
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ConversionFilesCallback
import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.conversion.*
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.widget.controller.UploadcareWidget

class CdnViewModel(application: Application) : AndroidViewModel(application) {

    val uploadcareFile = MutableLiveData<UploadcareFile>()

    val loading = MutableLiveData<Boolean>().apply { value = false }

    val status = MutableLiveData<String>()

    private val client = UploadcareWidget.getInstance().uploadcareClient

    private var converter: Converter? = null

    private var executor: AddOnExecutor? = null
    private val executorCallback = object : UploadcareFileCallback {
        override fun onFailure(e: UploadcareApiException) {
            showProgressOrResult(false, e.message)
        }

        override fun onSuccess(result: UploadcareFile) {
            showProgressOrResult(false, getContext().getString(R.string.cdn_status_execute_sucess))
            setFile(result)
        }
    }

    fun setFile(uploadcareFile: UploadcareFile?) {
        this.uploadcareFile.value = uploadcareFile
    }

    fun convertDocument() {
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_conversion_doc))
        uploadcareFile.value?.let {
            val conversionJob = DocumentConversionJob(it.uuid).apply {
                setFormat(DocumentFormat.JPG)
            }
            converter = DocumentConverter(client, listOf(conversionJob))
            converter?.convertAsync(object : ConversionFilesCallback {
                override fun onFailure(e: UploadcareApiException) {
                    showProgressOrResult(false, e.message)
                }

                override fun onSuccess(result: List<UploadcareFile>) {
                    showProgressOrResult(false, getContext().getString(R.string.cdn_status_sucess))
                    setFile(result.getOrNull(0))
                }
            })
        }
    }

    fun convertVideo() {
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_conversion_vid))
        uploadcareFile.value?.let {
            val conversionJob = VideoConversionJob(it.uuid).apply {
                setFormat(VideoFormat.MP4)
                quality(VideoQuality.NORMAL)
                thumbnails(2)
            }
            converter = VideoConverter(client, listOf(conversionJob))
            converter?.convertAsync(object : ConversionFilesCallback {
                override fun onFailure(e: UploadcareApiException) {
                    showProgressOrResult(false, e.message)
                }

                override fun onSuccess(result: List<UploadcareFile>) {
                    showProgressOrResult(false, getContext().getString(R.string.cdn_status_sucess))
                    setFile(result.getOrNull(0))
                }
            })
        }
    }

    fun cancel() {
        converter?.cancel()
        converter = null

        executor?.cancel()
        executor = null

        showProgressOrResult(false, "Canceled")
    }

    fun executeAWSRekognitionAddOn() {
        cancel()
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_execute_aws_rekognition))
        uploadcareFile.value?.let { file ->
            executor = AWSRekognitionAddOn(client).apply {
                executeAsync(file.uuid, executorCallback)
            }
        }
    }

    fun executeAWSRekognitionModerationAddOn() {
        cancel()
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_execute_aws_rekognition_moderation))
        uploadcareFile.value?.let { file ->
            executor = AWSRekognitionModerationAddOn(client).apply {
                executeAsync(file.uuid, executorCallback)
            }
        }
    }

    fun executeClamAVAddOn() {
        cancel()
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_execute_clam_av))
        uploadcareFile.value?.let { file ->
            executor = ClamAVAddOn(client).apply {
                executeAsync(file.uuid, executorCallback)
            }
        }
    }

    fun executeRemoveBgAddOn() {
        cancel()
        showProgressOrResult(true, getContext().getString(R.string.cdn_status_execute_remove_bg))
        uploadcareFile.value?.let { file ->
            executor = RemoveBgAddOn(client).apply {
                executeAsync(file.uuid, executorCallback)
            }
        }
    }

    /**
     * Shows current status message.
     *
     * @param progress `true` if request is in progress.
     * @param message  message to show.
     */
    private fun showProgressOrResult(progress: Boolean, message: String? = null) {
        if (!progress) {
            converter = null
        }
        loading.value = progress
        status.value = message
    }

    private fun getContext(): Context {
        return getApplication()
    }
}
