package com.uploadcare.android.library.data

import com.squareup.moshi.Json

internal data class ConvertData(
        val paths: List<String>,
        val store: String? = null,
        @Json(name = "save_in_group") val saveInGroup: String?
)

internal data class ConvertResultData(
        val problems: Map<String, String>,
        val result: List<ConvertResult>)

internal data class ConvertResult(
        val originalSource: String? = null,
        val uuid: String,
        val token: Int)

data class ConvertStatusData(
        val status: String,
        val result: ConvertStatusResultData,
        val error: String? = null)

data class ConvertStatusResultData(val uuid: String)
