package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

import java.util.List;

public interface UploadFilesCallback extends BaseCallback<List<UploadcareFile>> {

    void onFailure(UploadcareApiException e);

    void onSuccess(List<UploadcareFile> files);
}