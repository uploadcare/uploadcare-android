package com.uploadcare.android.widget.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedFile(@Json(name = "obj_type") val objectType: String,
                        @Json(name = "is_image") val isImage: Boolean? = false,
                        val url: String) : Parcelable