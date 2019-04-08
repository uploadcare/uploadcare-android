package com.uploadcare.android.widget.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChunkResponse(@Json(name = "obj_type") val objectType: String,
                         @Json(name = "login_link") val loginLink: String?,
                         @Json(name = "next_page") val nextPage: Path?,
                         @Json(name = "search_path") val searchPath: Chunk? = null,
                         val things: List<Thing>?,
                         val root: Chunk? = null,
                         val error: String? = null) : Parcelable

@Parcelize
data class Thing(@Json(name = "obj_type") val objectType: String,
                 val mimetype: String?,
                 val title: String?,
                 val action: Action?,
                 val thumbnail: String? = null) : Parcelable {

    companion object {
        const val TYPE_ALBUM = "album"
        const val TYPE_FOLDER = "folder"
        const val TYPE_FRIEND = "friend"
        const val TYPE_PHOTO = "photo"
        const val TYPE_FILE = "file"
    }
}