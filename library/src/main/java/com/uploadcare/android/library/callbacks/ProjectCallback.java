package com.uploadcare.android.library.callbacks;

import com.uploadcare.android.library.api.Project;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

public interface ProjectCallback extends BaseCallback<Project> {

    void onFailure(UploadcareApiException e);

    void onSuccess(Project project);
}