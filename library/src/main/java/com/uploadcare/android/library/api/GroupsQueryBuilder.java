package com.uploadcare.android.library.api;

import android.content.Context;
import android.os.AsyncTask;

import com.uploadcare.android.library.callbacks.UploadcareAllGroupsCallback;
import com.uploadcare.android.library.callbacks.UploadcareGroupsCallback;
import com.uploadcare.android.library.data.GroupPageData;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.urls.FilesFromParameter;
import com.uploadcare.android.library.urls.FilesLimitParameter;
import com.uploadcare.android.library.urls.FilesOrderParameter;
import com.uploadcare.android.library.urls.Order;
import com.uploadcare.android.library.urls.UrlParameter;
import com.uploadcare.android.library.urls.Urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Group resource request builder.
 *
 * Allows to specify some group filters and get results.
 *
 * {@link UploadcareGroup}
 */
public class GroupsQueryBuilder implements PaginatedQueryBuilder<UploadcareGroup> {

    private final UploadcareClient client;

    private final List<UrlParameter> parameters = new ArrayList<UrlParameter>();

    /**
     * Initializes a new builder for the given client.
     */
    public GroupsQueryBuilder(UploadcareClient client) {
        this.client = client;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    public GroupsQueryBuilder from(Date from) {
        parameters.add(new FilesFromParameter(from));
        return this;
    }

    /**
     * Specifies the way files are sorted.
     *
     * @param order {@link Order}
     */
    public GroupsQueryBuilder ordering(Order order) {
        parameters.add(new FilesOrderParameter(order));
        return this;
    }

    @Override
    public Iterable<UploadcareGroup> asIterable() {
        URI url = Urls.apiGroups();
        RequestHelper requestHelper = client.getRequestHelper();
        GroupDataWrapper dataWrapper = new GroupDataWrapper(client);
        return requestHelper
                .executePaginatedQuery(url, parameters, true, GroupPageData.class, dataWrapper);
    }

    @Override
    public List<UploadcareGroup> asList() {
        List<UploadcareGroup> groups = new ArrayList<>();
        for (UploadcareGroup file : asIterable()) {
            groups.add(file);
        }
        return groups;
    }

    /**
     * Returns a limited amount of group resources with specified offset Asynchronously.
     *
     * @param context  application context. @link android.content.Context
     * @param limit    amount of resources returned in callback.
     * @param next     amount of resources to skip. if {@code null} then no offset will be applied.
     * @param callback {@link UploadcareGroupsCallback}.
     */
    public void asListAsync(Context context, int limit, URI next,
                            UploadcareGroupsCallback callback) {
        if (next == null) {
            parameters.add(new FilesLimitParameter(limit));
        }
        URI url = (next == null) ? Urls.apiGroups() : next;
        RequestHelper requestHelper = client.getRequestHelper();
        GroupDataWrapper dataWrapper = new GroupDataWrapper(client);
        requestHelper.executeGroupsPaginatedQueryWithOffsetLimitAsync(context, url, parameters,
                true,
                dataWrapper,
                callback);
    }

    /**
     * Iterates through all resources and returns a complete list Asynchronously.
     *
     * @param callback {@link UploadcareAllGroupsCallback}.
     */
    public void asListAsync(UploadcareAllGroupsCallback callback) {
        new PaginatedQueryTask().execute(callback);
    }

    private class PaginatedQueryTask
            extends AsyncTask<UploadcareAllGroupsCallback, Void, List<UploadcareGroup>> {

        UploadcareAllGroupsCallback callback;

        @Override
        protected List<UploadcareGroup> doInBackground(UploadcareAllGroupsCallback... callbacks) {
            callback = callbacks[0];
            try {
                return asList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UploadcareGroup> result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(
                        new UploadcareApiException("Unexpected error"));
            }
        }
    }
}
