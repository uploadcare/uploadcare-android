package com.uploadcare.android.library.data;

import java.net.URI;
import java.util.List;

public class GroupPageData implements PageData<GroupData> {

    public URI next;

    public URI previous;

    public int total;

    public int perPage;

    public List<GroupData> results;

    public List<GroupData> getResults() {
        return results;
    }

    public boolean hasMore() {
        return next != null;
    }

    public URI getNext() {
        return next;
    }

}