package com.uploadcare.android.library.api;

import com.uploadcare.android.library.data.FileData;
import com.uploadcare.android.library.urls.CdnPathBuilder;

import java.net.URI;
import java.util.Date;

/**
 * The main Uploadcare resource, represents a user-uploaded file.
 */
public class UploadcareFile {

    private final UploadcareClient client;

    private final FileData fileData;

    UploadcareFile(UploadcareClient client, FileData fileData) {
        this.client = client;
        this.fileData = fileData;
    }

    public String getFileId() {
        return fileData.uuid;
    }

    public boolean isStored() {
        return fileData.datetimeStored != null;
    }

    public String getMimeType() {
        return fileData.mimeType;
    }

    public boolean hasOriginalFileUrl() {
        return fileData.originalFileUrl != null;
    }

    public URI getOriginalFileUrl() {
        return fileData.originalFileUrl;
    }

    public String getOriginalFilename() {
        return fileData.originalFilename;
    }

    public boolean isRemoved() {
        return fileData.datetimeRemoved != null;
    }

    public Date getRemoved() {
        return fileData.datetimeRemoved;
    }

    public int getSize() {
        return fileData.size;
    }

    public Date getUploadDate() {
        return fileData.datetimeUploaded;
    }

    /**
     * Returns the unique REST URL for this resource.
     *
     * @return REST URL
     */
    public URI getUrl() {
        return fileData.url;
    }

    /**
     * Refreshes file data from Uploadcare.
     *
     * This does not mutate the current {@code UploadcareFile} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public UploadcareFile update() {
        return client.getFile(fileData.uuid);
    }

    /**
     * Deletes this file from Uploadcare.
     *
     * This does not mutate the current {@code UploadcareFile} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public UploadcareFile delete() {
        client.deleteFile(fileData.uuid);
        return update();
    }

    /**
     * Saves this file on Uploadcare (marks it to be kept).
     *
     * This does not mutate the current {@link UploadcareFile} instance,
     * but creates a new one.
     *
     * @return New file resource instance
     */
    public UploadcareFile save() {
        client.saveFile(fileData.uuid);
        return update();
    }

    /**
     * Creates a CDN path builder for this file.
     *
     * @return CDN path builder
     * @see com.uploadcare.android.library.urls.Urls#cdn(CdnPathBuilder)
     */
    public CdnPathBuilder cdnPath() {
        return new CdnPathBuilder(this);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName()).append(NEW_LINE);
        result.append(" File id: ").append(getFileId()).append(NEW_LINE);
        result.append(" Original Filename: ").append(getOriginalFilename()).append(NEW_LINE);
        if(hasOriginalFileUrl()){
            result.append(" Original File Url: ").append(getOriginalFileUrl()).append(NEW_LINE);
        }
        result.append(" Size: ").append(getSize()).append(NEW_LINE);
        result.append(" is Stored: ").append(isStored()).append(NEW_LINE);
        result.append(" is Removed: ").append(isRemoved()).append(NEW_LINE);
        result.append(" Date uploaded: ").append(getUploadDate().toString()).append(NEW_LINE);
        if(isRemoved()){
            result.append(" Date removed: ").append(getRemoved().toString()).append(NEW_LINE);
        }
        return result.toString();

    }
}