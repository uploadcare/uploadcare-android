package com.uploadcare.android.library.exceptions;

public class UploadFailureException extends UploadcareApiException {

    public UploadFailureException() {
        super(new Exception("Upload failed"));
    }

    public UploadFailureException(String message) {
        super(new Exception(message));
    }

    public UploadFailureException(Exception e) {
        super(e);
    }
}
