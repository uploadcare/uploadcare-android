package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.DataWrapper;
import com.uploadcare.android.library.data.ProjectData;

public class ProjectDataWrapper implements DataWrapper<Project, ProjectData> {

    private final UploadcareClient client;

    public ProjectDataWrapper(UploadcareClient client) {
        this.client = client;
    }

    public Project wrap(ProjectData data) {
        return new Project(client, data);
    }

}