package com.uploadcare.android.library.data

import com.squareup.moshi.Json

data class DocumentInfo(
    // Holds an error if your document can't be handled.
    @Json(name = "error") val error: String?,

    // Document format details.
    @Json(name = "format") val format: DocumentFormat?,

    // Information about already converted groups.
    @Json(name = "converted_groups") val convertedGroups: ConvertedGroups?
)

data class DocumentFormat(
    // A detected document format.
    @Json(name = "name") val name: String?,

    // The conversions that are supported for the document.
    @Json(name = "conversion_formats") val conversionFormats: List<ConversionFormat?>?
)

data class ConversionFormat(
    // Supported target document format.
    @Json(name = "name") val name: String?
)

data class ConvertedGroups(
    @Json(name = "pdf") val pdf: String?
)