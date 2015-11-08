package com.uploadcare.android.library.urls;

public class FilesRemovedParameter implements UrlParameter {

    private final boolean removed;

    public FilesRemovedParameter(boolean removed) {
        this.removed = removed;
    }

    public String getParam() {
        return "removed";
    }

    public String getValue() {
        return removed ? "true" : "false";
    }
}