package com.uploadcare.android.library.api

import com.squareup.moshi.Json
import java.net.URI
import java.util.*

data class UploadcareGroup(val id: String,
                           val url: URI,
                           val files: List<UploadcareFile>? = null,
                           @Json(name = "datetime_created") val datetimeCreated: Date,
                           @Json(name = "datetime_stored") val datetimeStored: Date,
                           @Json(name = "files_count") val filesCount: Int,
                           @Json(name = "cdn_url") val cdnUrl: URI) {

    fun hasFiles() = files != null

    override fun toString(): String {
        val newline = System.getProperty("line.separator")
        return StringBuilder().apply {
            append(UploadcareGroup::class.simpleName).append(newline)
            append("Group id: $id").append(newline)
            append("Url: $url").append(newline)
            append("CDN url:: $cdnUrl").append(newline)
            append("Files count: : $filesCount").append(newline)
            append("Created: $datetimeCreated").append(newline)
            append("Stored: $datetimeStored").append(newline)
        }.toString()
    }
}