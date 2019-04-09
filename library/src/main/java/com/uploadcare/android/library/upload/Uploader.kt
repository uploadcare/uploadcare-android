package com.uploadcare.android.library.upload

import com.uploadcare.android.library.callbacks.UploadcareFileCallback
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.exceptions.UploadFailureException

interface Uploader {

    @Throws(UploadFailureException::class)
    fun upload(): UploadcareFile

    fun uploadAsync(callback: UploadcareFileCallback)

    fun store(store: Boolean): Uploader
}