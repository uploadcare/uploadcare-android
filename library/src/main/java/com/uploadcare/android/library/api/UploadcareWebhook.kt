package com.uploadcare.android.library.api

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.net.URI
import java.util.*

/**
 * The resource for Webhook.
 */
@Parcelize
data class UploadcareWebhook(val id: Int,
                             val event: String,
                             @Json(name = "target_url") val targetUrl: URI,
                             @Json(name = "is_active") val isActive: Boolean,
                             val project: Int,
                             val created: Date? = null,
                             val updated: Date? = null) : Parcelable
