package com.uploadcare.android.library.data

import com.squareup.moshi.Json

data class UploadFromUrlStatusData(val status: String,
                                   val done: Long = 0L,
                                   val total: Long = 0L,
                                   val error: String? = null,
                                   @Json(name = "file_id") val fileId: String?)