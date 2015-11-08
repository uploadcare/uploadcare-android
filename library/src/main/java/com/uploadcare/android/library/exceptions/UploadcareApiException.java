package com.uploadcare.android.library.exceptions;

/**
 * A generic error of the uploadcare API.
 */
public class UploadcareApiException extends RuntimeException {

    public UploadcareApiException(String message) {
        super(message);
    }

    public UploadcareApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadcareApiException(Throwable cause) {
        super(cause);
    }
}