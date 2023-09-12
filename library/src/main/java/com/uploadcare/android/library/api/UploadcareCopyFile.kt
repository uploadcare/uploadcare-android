package com.uploadcare.android.library.api

import android.os.Parcelable
import com.uploadcare.android.library.exceptions.UploadcareApiException
import com.uploadcare.android.library.utils.AsString
import kotlinx.parcelize.Parcelize
import java.net.URI

/**
 * Result of the file copy operation
 */
@Parcelize
@Suppress("unused")
data class UploadcareCopyFile(private val type: String,
                              @AsString private val result: String) : Parcelable {

    fun type(): CopyFileType {
        return CopyFileType.valueOf(type)
    }

    @Throws(UploadcareApiException::class)
    fun file(client: UploadcareClient): UploadcareFile? {
        if (type() != CopyFileType.FILE) {
            return null
        }

        return client.objectMapper.fromJson(result, UploadcareFile::class.java)
    }

    @Throws(UploadcareApiException::class)
    fun uri(client: UploadcareClient): URI? {
        if (type() != CopyFileType.URL) {
            return null
        }

        return client.objectMapper.fromJson(result, URI::class.java)
    }

    override fun toString(): String {
        val newline = System.getProperty("line.separator")
        return StringBuilder().apply {
            append(UploadcareCopyFile::class.simpleName).append(newline)
            append("type: $type").append(newline)
            append("result: $result").append(newline)
        }.toString()
    }
}

@Suppress("unused")
enum class CopyFileType constructor(val rawValue: String) {
    URL("url"),
    FILE("file")
}
