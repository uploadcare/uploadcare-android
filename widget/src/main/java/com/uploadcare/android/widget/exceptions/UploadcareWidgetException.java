package com.uploadcare.android.widget.exceptions;

public class UploadcareWidgetException extends RuntimeException {
    public UploadcareWidgetException(String message) {
        super(message);
    }

    public UploadcareWidgetException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadcareWidgetException(Throwable cause) {
        super(cause);
    }
}
