package com.uploadcare.android.widget.exceptions

/**
 * Error returned by uploadcare Widget.
 */
@Suppress("unused")
class UploadcareWidgetException : RuntimeException {
    constructor(message: String? = null) : super(message)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}