package com.uploadcare.android.library.urls;

import android.net.Uri;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {

    public static URI trustedBuild(Uri.Builder builder) {
        try {
            return new URI(builder.build().toString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}