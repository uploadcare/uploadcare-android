package com.uploadcare.android.library.upload

import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.callbacks.UploadFilesCallback
import com.uploadcare.android.library.exceptions.UploadFailureException

interface MultipleUploader {

    @Throws(UploadFailureException::class)
    fun upload(progressCallback: ProgressCallback? = null): List<UploadcareFile>

    fun uploadAsync(callback: UploadFilesCallback)

    fun store(store: Boolean): MultipleUploader
}