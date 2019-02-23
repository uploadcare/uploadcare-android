package com.uploadcare.android.library.urls;

public class FilesOrderParameter implements UrlParameter {

    private final Order ordering;

    public FilesOrderParameter(Order order) {
        this.ordering = order;
    }

    public String getParam() {
        return "ordering";
    }

    public String getValue() {
        return ordering.toString();
    }
}
