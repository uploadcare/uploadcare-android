package com.uploadcare.android.library.data

internal data class CopyOptionsData(val source: String,
                           val target: String? = null,
                           val store: Boolean? = null,
                           val makePublic: Boolean? = null,
                           val pattern: String? = null)