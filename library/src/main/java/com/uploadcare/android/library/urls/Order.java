package com.uploadcare.android.library.urls;

public enum Order {
    UPLOAD_TIME_ASC("datetime_uploaded"),
    UPLOAD_TIME_DESC("-datetime_uploaded"),
    SIZE_ASC("size"),
    SIZE_DESC("-size");

    private final String stringValue;

    Order(final String value) {
        stringValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
