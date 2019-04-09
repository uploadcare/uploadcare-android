package com.uploadcare.android.library.urls

import android.net.Uri
import java.net.URI
import java.net.URISyntaxException

class UrlUtils {
    companion object {

        fun trustedBuild(builder: Uri.Builder): URI {
            try {
                return URI(builder.build().toString())
            } catch (e: URISyntaxException) {
                throw IllegalArgumentException(e)
            }
        }
    }
}