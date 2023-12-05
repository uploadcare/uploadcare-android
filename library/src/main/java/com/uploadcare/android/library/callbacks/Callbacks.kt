package com.uploadcare.android.library.callbacks

import androidx.annotation.NonNull
import com.uploadcare.android.library.api.*
import com.uploadcare.android.library.exceptions.UploadcareApiException
import okhttp3.Response
import java.net.URI

interface BaseCallback<T> {

    fun onFailure(@NonNull e: UploadcareApiException)

    fun onSuccess(@NonNull result: @JvmSuppressWildcards T)
}

interface BaseListCallback<T> : BaseCallback<List<@JvmSuppressWildcards T>>

interface BasePaginationCallback<T> {

    fun onFailure(@NonNull e: UploadcareApiException)

    fun onSuccess(@NonNull result: List<@JvmSuppressWildcards T>, next: URI?)
}

interface ProgressCallback {
    fun onProgressUpdate(bytesWritten: Long, contentLength: Long, progress: Double)
}

interface RequestCallback : BaseCallback<Response>

interface CopyFileCallback : BaseCallback<UploadcareCopyFile>

interface ProjectCallback : BaseCallback<Project>

interface UploadcareFileCallback : BaseCallback<UploadcareFile>

interface UploadFileCallback : BaseCallback<UploadcareFile>, ProgressCallback

interface UploadcareFilesCallback : BasePaginationCallback<UploadcareFile>

interface UploadcareAllFilesCallback : BaseListCallback<UploadcareFile>

interface UploadcareGroupCallback : BaseCallback<UploadcareGroup>

interface UploadcareGroupsCallback : BasePaginationCallback<UploadcareGroup>

interface UploadcareAllGroupsCallback : BaseListCallback<UploadcareGroup>

interface UploadFilesCallback : BaseListCallback<UploadcareFile>, ProgressCallback

interface ConversionFilesCallback : BaseListCallback<UploadcareFile>

interface UploadcareWebhookCallback : BaseCallback<UploadcareWebhook>

interface UploadcareWebhooksCallback : BaseListCallback<UploadcareWebhook>

interface UploadcareMetadataCallback : BaseCallback<Map<String, String>>

interface UploadcareMetadataKeyValueCallback : BaseCallback<String>
