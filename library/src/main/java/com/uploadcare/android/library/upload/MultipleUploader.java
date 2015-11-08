package com.uploadcare.android.library.upload;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadFilesCallback;
import com.uploadcare.android.library.exceptions.UploadFailureException;

import java.util.List;

public interface MultipleUploader {

    List<UploadcareFile> upload() throws UploadFailureException;

    void uploadAsync(UploadFilesCallback callback) throws UploadFailureException;

    MultipleUploader store(boolean store);
}