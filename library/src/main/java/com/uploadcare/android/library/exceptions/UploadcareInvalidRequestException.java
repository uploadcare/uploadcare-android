package com.uploadcare.android.library.exceptions;

/**
 * Error produced in case the http request sent to the Uploadcare API was invalid.
 */
public class UploadcareInvalidRequestException extends UploadcareApiException {

    public UploadcareInvalidRequestException(String message) {
        super(message);
    }
}