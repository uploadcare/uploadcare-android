package com.uploadcare.android.library.data;

import java.net.URI;
import java.util.List;

public class FilePageData implements PageData<FileData> {

    public URI next;

    public URI previous;

    public int total;

    public int perPage;

    public List<FileData> results;

    public List<FileData> getResults() {
        return results;
    }

    public boolean hasMore() {
        return next != null;
    }

    public URI getNext(){
        return next;
    }

}