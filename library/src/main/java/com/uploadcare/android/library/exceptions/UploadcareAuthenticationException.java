package com.uploadcare.android.library.exceptions;

/**
 * An authentication error returned by the uploadcare API
 */
public class UploadcareAuthenticationException extends UploadcareApiException {

    public UploadcareAuthenticationException(String message) {
        super(message);
    }
}