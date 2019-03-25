package com.uploadcare.android.library.data

import com.squareup.moshi.Json
import com.uploadcare.android.library.api.UploadcareGroup
import java.net.URI

data class GroupPageData(val next: URI? = null,
                         val previous: URI? = null,
                         val total: Int,
                         @Json(name = "per_page") val perPage: Int,
                         val results: List<UploadcareGroup>) : PageData<UploadcareGroup> {

    override fun getResultsData() = results

    override fun hasMore() = next != null

    override fun getNextURI() = next
}