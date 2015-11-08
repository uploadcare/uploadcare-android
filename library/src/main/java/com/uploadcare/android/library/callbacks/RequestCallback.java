package com.uploadcare.android.library.callbacks;

import com.squareup.okhttp.Response;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

public interface RequestCallback extends BaseCallback<Response> {

    void onFailure(UploadcareApiException e);

    void onSuccess(Response response);
}
