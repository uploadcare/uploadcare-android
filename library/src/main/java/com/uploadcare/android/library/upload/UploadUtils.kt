package com.uploadcare.android.library.upload

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import okhttp3.MediaType
import java.io.File
import java.io.InputStream

class UploadUtils {
    companion object {

        private val MEDIA_TYPE_TEXT_PLAIN = MediaType.parse("text/plain")

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
            return MediaType.parse(type)
        }

        fun getMimeType(contentResolver: ContentResolver, uri: Uri?): MediaType? {
            if (uri == null) {
                return MEDIA_TYPE_TEXT_PLAIN
            }

            if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                return MediaType.parse(contentResolver.getType(uri))
            } else {
                val mime = MimeTypeMap.getSingleton()
                //This will replace white spaces with %20 and also other special characters.
                // This will avoid returning null values on file name with spaces and special characters.
                val extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path!!)).toString())
                val type = mime.getMimeTypeFromExtension(extension) ?: return MEDIA_TYPE_TEXT_PLAIN
                return MediaType.parse(type)
            }
        }
    }
}
