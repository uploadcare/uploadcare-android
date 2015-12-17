package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.DataWrapper;
import com.uploadcare.android.library.data.GroupData;


public class GroupDataWrapper implements DataWrapper<UploadcareGroup, GroupData> {

    private final UploadcareClient client;

    public GroupDataWrapper(UploadcareClient client) {
        this.client = client;
    }

    public UploadcareGroup wrap(GroupData data) {
        return new UploadcareGroup(client, data);
    }

}
