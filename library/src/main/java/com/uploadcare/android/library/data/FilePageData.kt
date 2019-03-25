package com.uploadcare.android.library.data

import com.squareup.moshi.Json
import com.uploadcare.android.library.api.UploadcareFile
import java.net.URI

data class FilePageData(val next: URI? = null,
                        val previous: URI? = null,
                        val total: Int,
                        @Json(name = "per_page") val perPage: Int,
                        val results: List<UploadcareFile>) : PageData<UploadcareFile> {

    override fun getResultsData() = results

    override fun hasMore() = next != null

    override fun getNextURI() = next
}