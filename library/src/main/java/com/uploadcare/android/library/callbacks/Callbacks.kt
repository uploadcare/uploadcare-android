package com.uploadcare.android.library.callbacks

import androidx.annotation.NonNull
import com.uploadcare.android.library.api.Project
import com.uploadcare.android.library.api.UploadcareCopyFile
import com.uploadcare.android.library.api.UploadcareFile
import com.uploadcare.android.library.api.UploadcareGroup
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

interface RequestCallback : BaseCallback<Response>

interface CopyFileCallback : BaseCallback<UploadcareCopyFile>

interface ProjectCallback : BaseCallback<Project>

interface UploadcareFileCallback : BaseCallback<UploadcareFile>

interface UploadcareFilesCallback : BasePaginationCallback<UploadcareFile>

interface UploadcareAllFilesCallback : BaseListCallback<UploadcareFile>

interface UploadcareGroupCallback : BaseCallback<UploadcareGroup>

interface UploadcareGroupsCallback : BasePaginationCallback<UploadcareGroup>

interface UploadcareAllGroupsCallback : BaseListCallback<UploadcareGroup>

interface UploadFilesCallback : BaseListCallback<UploadcareFile>