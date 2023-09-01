package com.uploadcare.android.library.exceptions

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadcareException(val message: String? = "Upload failed") : Parcelable

/**
 * A generic error of the uploadcare API.
 */
open class UploadcareApiException : RuntimeException {
    constructor(message: String? = null) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * An authentication error returned by the uploadcare API
 */
class UploadcareAuthenticationException(message: String?) : UploadcareApiException(message)

/**
 * Error produced in case the http request sent to the Uploadcare API was invalid.
 */
class UploadcareInvalidRequestException(message: String?) : UploadcareApiException(message)

class UploadFailureException : UploadcareApiException {
    constructor(message: String? = "Upload failed") : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

internal class UploadPausedException(message: String?): UploadcareApiException(message)
