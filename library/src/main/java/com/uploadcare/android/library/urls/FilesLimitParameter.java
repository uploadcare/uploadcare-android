package com.uploadcare.android.library.urls;

public class FilesLimitParameter implements UrlParameter {

    private final int limit;

    public FilesLimitParameter(int limit) {
        this.limit = limit;
    }

    public String getParam() {
        return "limit";
    }

    public String getValue() {
        return Integer.toString(this.limit);
    }
}