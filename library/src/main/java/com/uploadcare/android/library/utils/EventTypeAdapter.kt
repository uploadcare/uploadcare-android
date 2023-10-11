package com.uploadcare.android.library.utils

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.uploadcare.android.library.api.EventType

internal class EventTypeAdapter {

    @ToJson
    fun toJson(eventType: EventType): String = eventType.value

    @FromJson
    fun fromJson(value: String): EventType = EventType.values()
        .firstOrNull { eventType -> eventType.value == value }
        ?: throw IllegalArgumentException("Unknown event type")
}
