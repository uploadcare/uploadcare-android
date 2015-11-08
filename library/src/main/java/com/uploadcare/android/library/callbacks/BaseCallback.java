package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.exceptions.UploadcareApiException;

public interface BaseCallback<T> {

    void onFailure(UploadcareApiException e);

    void onSuccess(T result);
}
