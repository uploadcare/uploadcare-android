package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.DataWrapper;
import com.uploadcare.android.library.data.FileData;

public class FileDataWrapper implements DataWrapper<UploadcareFile, FileData> {

    private final UploadcareClient client;

    public FileDataWrapper(UploadcareClient client) {
        this.client = client;
    }

    public UploadcareFile wrap(FileData data) {
        return new UploadcareFile(client, data);
    }

}