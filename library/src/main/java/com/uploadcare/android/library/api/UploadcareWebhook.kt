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
data class UploadcareWebhook(
    val id: Int,
    val event: EventType,
    @Json(name = "target_url") val targetUrl: URI,
    @Json(name = "is_active") val isActive: Boolean,
    val project: Int,
    val created: Date? = null,
    val updated: Date? = null,
    @Json(name = "signing_secret") val signingSecret: String? = null
) : Parcelable

enum class EventType(val value: String) {
    UPLOADED("file.uploaded"),
    INFECTED("file.infected"),
    STORED("file.stored"),
    DELETED("file.deleted"),
    INFO_UPDATED("file.info_updated");
}
