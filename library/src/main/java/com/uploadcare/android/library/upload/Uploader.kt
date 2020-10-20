package com.uploadcare.android.library.upload

import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.callbacks.ProgressCallback
import com.uploadcare.android.library.callbacks.UploadFileCallback
import com.uploadcare.android.library.exceptions.UploadFailureException

interface Uploader {

    @Throws(UploadFailureException::class)
    fun upload(progressCallback: ProgressCallback? = null): UploadcareFile

    fun uploadAsync(callback: UploadFileCallback)

    fun cancel()

    fun store(store: Boolean): Uploader
}