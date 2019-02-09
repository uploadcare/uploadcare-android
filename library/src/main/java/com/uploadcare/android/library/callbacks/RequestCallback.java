package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.exceptions.UploadcareApiException;

import okhttp3.Response;

public interface RequestCallback extends BaseCallback<Response> {

    void onFailure(UploadcareApiException e);

    void onSuccess(Response response);
}
