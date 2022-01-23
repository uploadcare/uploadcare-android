package com.uploadcare.android.widget.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chunk(@Json(name = "obj_type") val objectType: String,
                 @Json(name = "path_chunk") val pathChunk: String,
                 val title: String) : Parcelable