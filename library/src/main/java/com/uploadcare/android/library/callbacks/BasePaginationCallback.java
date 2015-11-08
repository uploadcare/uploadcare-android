package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.exceptions.UploadcareApiException;

import java.net.URI;


public interface BasePaginationCallback<T> {

    void onFailure(UploadcareApiException e);

    void onSuccess(T result, URI next);
}
