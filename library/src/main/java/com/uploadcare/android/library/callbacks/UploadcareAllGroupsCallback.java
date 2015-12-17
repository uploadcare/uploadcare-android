package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.UploadcareGroup;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

import java.util.List;

public interface UploadcareAllGroupsCallback extends BaseCallback<List<UploadcareGroup>> {

    void onFailure(UploadcareApiException e);

    void onSuccess(List<UploadcareGroup> groups);
}
