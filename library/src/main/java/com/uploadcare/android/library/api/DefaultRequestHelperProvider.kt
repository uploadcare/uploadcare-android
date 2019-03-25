package com.uploadcare.android.library.api

class DefaultRequestHelperProvider : RequestHelperProvider {

    override fun get(client: UploadcareClient): RequestHelper {
        return RequestHelper(client)
    }
}