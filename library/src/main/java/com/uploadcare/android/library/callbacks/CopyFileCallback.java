package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.data.CopyFileData;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

public interface CopyFileCallback extends BaseCallback<CopyFileData> {

    void onFailure(UploadcareApiException e);

    void onSuccess(CopyFileData copyFileData);
}