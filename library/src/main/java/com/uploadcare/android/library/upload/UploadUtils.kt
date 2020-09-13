package com.uploadcare.android.library.upload

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.uploadcare.android.library.exceptions.UploadFailureException
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.InputStream

class UploadUtils {
    companion object {

        private val MEDIA_TYPE_TEXT_PLAIN = "text/plain".toMediaTypeOrNull()

        fun getBytes(inputStream: InputStream?): ByteArray? {
            return inputStream?.readBytes()
        }

        fun getFileName(uri: Uri, context: Context): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        fun getFileSize(uri: Uri, context: Context): Long? {
            var result: Long? = null

            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
                    }
                } finally {
                    cursor?.close()
                }
            }

            return result
        }

        fun getMimeType(file: File?): MediaType? {
            return if (file == null) {
                MEDIA_TYPE_TEXT_PLAIN
            } else {
                getMimeType(file.name)
            }
        }

        fun getMimeType(fileName: String?): MediaType? {
            if (fileName == null) {
                return MEDIA_TYPE_TEXT_PLAIN
            }

            val mime = MimeTypeMap.getSingleton()
            val index = fileName.lastIndexOf('.') + 1
            val ext = fileName.substring(index).toLowerCase()
            val type = mime.getMimeTypeFromExtension(ext) ?: return MEDIA_TYPE_TEXT_PLAIN
            return type.toMediaTypeOrNull()
        }

        fun getMimeType(contentResolver: ContentResolver, uri: Uri?): MediaType? {
            if (uri == null) {
                return MEDIA_TYPE_TEXT_PLAIN
            }

            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                return contentResolver.getType(uri)?.toMediaTypeOrNull()
            } else {
                val mime = MimeTypeMap.getSingleton()
                //This will replace white spaces with %20 and also other special characters.
                // This will avoid returning null values on file name with spaces and special characters.
                val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
                val type = mime.getMimeTypeFromExtension(extension) ?: return MEDIA_TYPE_TEXT_PLAIN
                return type.toMediaTypeOrNull()
            }
        }

        fun File.chunkedSequence(chunk: Int): Sequence<ByteArray> {
            val input = this.inputStream().buffered()
            val buffer = ByteArray(chunk)
            return generateSequence {
                val red = input.read(buffer)
                if (red >= 0) buffer.copyOf(red)
                else {
                    input.close()
                    null
                }
            }
        }

        fun InputStream.chunkedSequence(chunk: Int): Sequence<ByteArray> {
            val input = this.buffered()
            val buffer = ByteArray(chunk)
            return generateSequence {
                val red = input.read(buffer)
                if (red >= 0) buffer.copyOf(red)
                else {
                    input.close()
                    null
                }
            }
        }

        fun Uri.chunkedSequence(context: Context, chunk: Int): Sequence<ByteArray> {
            val input = context.contentResolver?.openInputStream(this)?.buffered()
                    ?: throw UploadFailureException(IllegalArgumentException())
            val buffer = ByteArray(chunk)
            return generateSequence {
                val red = input.read(buffer)
                if (red >= 0) buffer.copyOf(red)
                else {
                    input.close()
                    null
                }
            }
        }

        fun String.chunkedSequence(chunk: Int): Sequence<ByteArray> {
            val input = this.byteInputStream()
            val buffer = ByteArray(chunk)
            return generateSequence {
                val red = input.read(buffer)
                if (red >= 0) buffer.copyOf(red)
                else {
                    input.close()
                    null
                }
            }
        }

        fun ByteArray.chunkedSequence(chunk: Int): Sequence<ByteArray> {
            val input = this.inputStream()
            val buffer = ByteArray(chunk)
            return generateSequence {
                val red = input.read(buffer)
                if (red >= 0) buffer.copyOf(red)
                else {
                    input.close()
                    null
                }
            }
        }
    }
}

internal data class UploadProgress(
        val bytesWritten: Long,
        val contentLength: Long,
        val progress: Double)