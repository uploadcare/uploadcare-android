package com.uploadcare.android.library.data

import com.squareup.moshi.Json

data class AddOnExecuteData(val target: String)

data class AddOnExecuteResult(@Json(name = "request_id") val requestId: String)

data class AddOnStatusResult(val status: AddOnStatus, val result: AddOnStatusResultData?)

data class AddOnStatusResultData(@Json(name = "file_id") val fileId: String)

enum class AddOnStatus(val value: String) {
    IN_PROGRESS("in_progress"),
    ERROR("error"),
    DONE("done"),
    UNKNOWN("unknown")
}
