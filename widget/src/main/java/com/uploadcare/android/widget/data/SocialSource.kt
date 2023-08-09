package com.uploadcare.android.widget.data

import android.content.Context
import android.os.Parcelable
import android.preference.PreferenceManager
import android.webkit.CookieManager
import com.squareup.moshi.Json
import com.uploadcare.android.widget.R
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SocialSource(@Json(name = "root_chunks") val rootChunks: List<Chunk>,
                        val name: String,
                        val urls: @RawValue Urls) : Parcelable {

    fun saveCookie(context: Context, cookie: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString("UCW_PREF_$name", cookie)
                .apply()
    }

    fun getCookie(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("UCW_PREF_$name", null) ?: ""
    }

    fun deleteCookie(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        CookieManager.getInstance().removeAllCookies(null)
        preferences.edit().remove("UCW_PREF_$name").apply()
    }

    fun getNetworkNameResource(): Int {
        return when (name) {
            "facebook" -> R.string.ucw_social_facebook
            "instagram" -> R.string.ucw_social_instagram
            "vk" -> R.string.ucw_social_vk
            "box" -> R.string.ucw_social_box
            "huddle" -> R.string.ucw_social_huddle
            "flickr" -> R.string.ucw_social_flickr
            "evernote" -> R.string.ucw_social_evernote
            "skydrive" -> R.string.ucw_social_skydrive
            "dropbox" -> R.string.ucw_social_dropbox
            "gdrive" -> R.string.ucw_social_gdrive
            "video" -> R.string.ucw_social_video
            "image" -> R.string.ucw_social_image
            "file" -> R.string.ucw_social_file
            "onedrive" -> R.string.ucw_social_onedrive
            "gphotos" -> R.string.ucw_social_gphotos
            else -> R.string.ucw_social_unknown
        }
    }

    fun getNetworkIconResource(): Int {
        return when (name) {
            "facebook" -> R.drawable.ucw_facebook_icon
            "instagram" -> R.drawable.ucw_instagram_icon
            "vk" -> R.drawable.ucw_vkontakte_icon
            "box" -> R.drawable.ucw_box_icon
            "huddle" -> R.drawable.ucw_huddle_icon
            "flickr" -> R.drawable.ucw_flickr_icon
            "evernote" -> R.drawable.ucw_evenote_icon
            "skydrive" -> R.drawable.ucw_onedrive_icon
            "dropbox" -> R.drawable.ucw_dropbox_icon
            "gdrive" -> R.drawable.ucw_googledrive_icon
            "video" -> R.drawable.ic_videocam_white_24dp
            "image" -> R.drawable.ic_photo_camera_white_24dp
            "file" -> R.drawable.ic_insert_photo_white_24dp
            "onedrive" -> R.drawable.ucw_onedrive_icon
            "gphotos" -> R.drawable.ucw_gphotos_icon
            else -> -1
        }
    }
}

@Parcelize
data class Urls(@Json(name = "source_base") val sourceBase: String,
                val session: String,
                val done: String) : Parcelable
