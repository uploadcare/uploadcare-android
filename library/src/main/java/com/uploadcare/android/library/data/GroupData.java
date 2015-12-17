package com.uploadcare.android.library.data;

import java.net.URI;
import java.util.Date;
import java.util.List;

public class GroupData {

    public Date datetimeCreated;

    public int filesCount;

    public URI cdnUrl;

    public List<FileData> files;

    public URI url;

    public String id;
}
