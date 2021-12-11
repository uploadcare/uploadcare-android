package com.uploadcare.android.library.data

import com.squareup.moshi.Json
import java.net.URI

internal data class WebhookOptionsData(@Json(name = "target_url") val targetUrl: URI,
                                       val event: String? = null,
                                       @Json(name = "is_active") val isActive: Boolean? = null,
                                       @Json(name = "signing_secret") val signingSecret: String? = null)