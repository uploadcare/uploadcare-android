package com.uploadcare.android.library.data

import com.squareup.moshi.Json

internal data class CopyOptionsData(val source: String,
                                    val target: String? = null,
                                    val store: Boolean? = null,
                                    @Json(name = "make_public") val makePublic: Boolean? = null,
                                    val pattern: String? = null)