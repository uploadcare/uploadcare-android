package com.uploadcare.android.library.data

internal data class ConvertData(val paths: List<String>, val store: String? = null)

internal data class ConvertResultData(
        val problems: Map<String, String>,
        val result: List<ConvertResult>)

internal data class ConvertResult(
        val originalSource: String? = null,
        val uuid: String,
        val token: Int)

internal data class ConvertStatusData(
        val status: String,
        val result: ConvertStatusResultData,
        val error: String? = null)

internal data class ConvertStatusResultData(val uuid: String)