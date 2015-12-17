package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.FileData;
import com.uploadcare.android.library.data.GroupData;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The main Uploadcare resource, represents a group.
 */
public class UploadcareGroup {

    private final UploadcareClient client;

    private final GroupData groupData;

    UploadcareGroup(UploadcareClient client, GroupData groupData) {
        this.client = client;
        this.groupData = groupData;
    }

    public String getGroupId() {
        return groupData.id;
    }


    public int getFilesCount() {
        return groupData.filesCount;
    }

    /**
     * Returns the CDN URL for this resource.
     *
     * @return CDN URL
     */
    public URI getCdnUrl() {
        return groupData.cdnUrl;
    }

    /**
     * Returns the unique REST URL for this resource.
     *
     * @return REST URL
     */
    public URI getUrl() {
        return groupData.url;
    }

    public Date getDateCreated() {
        return groupData.datetimeCreated;
    }

    public boolean hasFiles() {
        return (groupData.files != null);
    }

    public List<UploadcareFile> getFiles() {
        if (groupData.files == null) {
            return null;
        }
        List<UploadcareFile> files = new ArrayList<>();
        for (FileData fileData : groupData.files) {
            if (fileData != null) {
                FileDataWrapper wrapper = new FileDataWrapper(client);
                files.add(wrapper.wrap(fileData));
            }
        }
        return files;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        result.append(this.getClass().getName()).append(NEW_LINE);
        result.append(" Group id: ").append(getGroupId()).append(NEW_LINE);
        result.append(" Files count: ").append(getFilesCount()).append(NEW_LINE);
        result.append(" created: ").append(getDateCreated()).append(NEW_LINE);
        result.append(" url: ").append(getUrl()).append(NEW_LINE);
        result.append(" CDN url: ").append(getCdnUrl()).append(NEW_LINE);
        return result.toString();

    }
}
