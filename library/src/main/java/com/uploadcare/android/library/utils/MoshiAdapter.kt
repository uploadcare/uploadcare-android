package com.uploadcare.android.library.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.net.URI

class MoshiAdapter {
    @FromJson
    fun uriFromJson(uriJson: String): URI {
        return URI(uriJson)
    }

    @ToJson
    fun uriToJson(uri: URI): String {
        return uri.toString()
    }
}