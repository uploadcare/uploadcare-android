package com.uploadcare.android.widget.utils

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable

internal fun <T : Parcelable> Bundle.getSupportParcelable(key: String, clazz: Class<T>): T? =
    if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        getParcelable(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key)
    }

internal fun <T : Parcelable> Bundle.getSupportParcelableArrayList(
    key: String,
    clazz: Class<T>
): ArrayList<T>? =
    if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        getParcelableArrayList(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        getParcelableArrayList(key)
    }
