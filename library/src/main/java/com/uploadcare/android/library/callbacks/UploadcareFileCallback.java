package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

public interface UploadcareFileCallback extends BaseCallback<UploadcareFile> {

    void onFailure(UploadcareApiException e);

    void onSuccess(UploadcareFile file);
}
