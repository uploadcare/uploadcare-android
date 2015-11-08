package com.uploadcare.android.library.upload;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadFailureException;

public interface Uploader {

    UploadcareFile upload() throws UploadFailureException;

    void uploadAsync(UploadcareFileCallback callback) throws UploadFailureException;

    Uploader store(boolean store);
}
