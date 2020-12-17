package com.uploadcare.android.library.conversion

import com.uploadcare.android.library.api.UploadcareClient
import com.uploadcare.android.library.urls.Urls
import java.net.URI

/**
 * Uploadcare converter for DocumentConversionJob's.
 */
class DocumentConverter(client: UploadcareClient, conversionJobs: List<DocumentConversionJob>)
    : Converter(client, conversionJobs) {

    override fun getConversionUri(): URI {
        return Urls.apiConvertDocument()
    }

    override fun getConversionStatusUri(token: Int): URI {
        return Urls.apiConvertDocumentStatus(token)
    }
}