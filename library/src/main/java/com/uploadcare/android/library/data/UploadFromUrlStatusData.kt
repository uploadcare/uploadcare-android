package com.uploadcare.android.library.data

import com.squareup.moshi.Json

data class UploadFromUrlStatusData(val status: String,
                                   @Json(name = "file_id") val fileId: String?)