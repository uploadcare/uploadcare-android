package com.uploadcare.android.library.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.uploadcare.android.library.data.AddOnStatus

internal object AddOnStatusAdapter {

    @ToJson
    fun toJson(type: AddOnStatus): String = type.value

    @FromJson
    fun fromJson(value: String): AddOnStatus = AddOnStatus.entries
        .firstOrNull { type -> type.value == value }
        ?: throw IllegalArgumentException("Unknown status type")
}
