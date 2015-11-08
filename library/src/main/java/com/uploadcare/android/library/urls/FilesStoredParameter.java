package com.uploadcare.android.library.urls;

public class FilesStoredParameter implements UrlParameter {

    private final boolean stored;

    public FilesStoredParameter(boolean stored) {
        this.stored = stored;
    }

    public String getParam() {
        return "stored";
    }

    public String getValue() {
        return stored ? "true" : "false";
    }
}