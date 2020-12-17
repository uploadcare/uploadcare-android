package com.uploadcare.android.widget.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize @Suppress("unused")
data class Action(@Json(name = "obj_type") val objectType: String,
                  val action: String?,
                  val url: String?,
                  val path: Path?) : Parcelable {
    companion object {
        const val ACTION_OPEN_PATH = "open_path"
        const val ACTION_SELECT_FILE = "select_file"
    }
}

@Parcelize
data class Path(@Json(name = "obj_type") val objectType: String,
                val chunks: List<Chunk>?) : Parcelable