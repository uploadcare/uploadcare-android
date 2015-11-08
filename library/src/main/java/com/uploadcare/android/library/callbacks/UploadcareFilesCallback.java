package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

import java.net.URI;
import java.util.List;

public interface UploadcareFilesCallback extends BasePaginationCallback<List<UploadcareFile>>{

    void onFailure(UploadcareApiException e);

    void onSuccess(List<UploadcareFile> files, URI next);
}
