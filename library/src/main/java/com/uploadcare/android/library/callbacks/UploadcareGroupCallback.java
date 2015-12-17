package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.UploadcareGroup;
import com.uploadcare.android.library.exceptions.UploadcareApiException;


public interface UploadcareGroupCallback extends BaseCallback<UploadcareGroup> {

    void onFailure(UploadcareApiException e);

    void onSuccess(UploadcareGroup group);
}
