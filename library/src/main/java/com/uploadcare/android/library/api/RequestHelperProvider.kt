package com.uploadcare.android.library.api

interface RequestHelperProvider {

    fun get(client: UploadcareClient): RequestHelper
}