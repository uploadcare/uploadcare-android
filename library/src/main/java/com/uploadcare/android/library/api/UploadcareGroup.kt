package com.uploadcare.android.library.api

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.net.URI
import java.util.*

@Parcelize @Suppress("unused")
data class UploadcareGroup(val id: String,
                           val url: URI,
                           val files: List<UploadcareFile>? = null,
                           @Json(name = "datetime_created") val datetimeCreated: Date?,
                           @Json(name = "datetime_stored") val datetimeStored: Date?,
                           @Json(name = "files_count") val filesCount: Int = 0,
                           @Json(name = "cdn_url") val cdnUrl: URI) : Parcelable {

    fun hasFiles() = files != null

    /**
     * Mark all files in a group as stored. Sync operation.
     *
     * @return Updated Group resource instance
     */
    fun store(client: UploadcareClient): UploadcareGroup? {
        client.storeGroup(id)
        return client.getGroup(id)
    }

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
