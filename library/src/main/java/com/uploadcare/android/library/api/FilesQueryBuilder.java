package com.uploadcare.android.library.api;

import com.uploadcare.android.library.callbacks.UploadcareAllFilesCallback;
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback;
import com.uploadcare.android.library.data.FilePageData;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.urls.FilesFromParameter;
import com.uploadcare.android.library.urls.FilesLimitParameter;
import com.uploadcare.android.library.urls.FilesRemovedParameter;
import com.uploadcare.android.library.urls.FilesStoredParameter;
import com.uploadcare.android.library.urls.FilesToParameter;
import com.uploadcare.android.library.urls.UrlParameter;
import com.uploadcare.android.library.urls.Urls;

import android.content.Context;
import android.os.AsyncTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File resource request builder.
 *
 * Allows to specify some file filters and get results.
 *
 * {@link UploadcareFile}
 */
public class FilesQueryBuilder implements PaginatedQueryBuilder<UploadcareFile> {

    private final UploadcareClient client;

    private final List<UrlParameter> parameters = new ArrayList<UrlParameter>();

    /**
     * Initializes a new builder for the given client.
     */
    public FilesQueryBuilder(UploadcareClient client) {
        this.client = client;
    }

    /**
     * Adds a filter for removed files.
     *
     * @param removed If {@code true}, accepts removed files, otherwise declines them.
     */
    public FilesQueryBuilder removed(boolean removed) {
        parameters.add(new FilesRemovedParameter(removed));
        return this;
    }

    /**
     * Adds a filter for stored files.
     *
     * @param stored If {@code true}, accepts stored files, otherwise declines them.
     */
    public FilesQueryBuilder stored(boolean stored) {
        parameters.add(new FilesStoredParameter(stored));
        return this;
    }

    /**
     * Adds a filter for datetime from objects will be returned.
     *
     * @param from A uploading datetime from which objects will be returned.
     */
    public FilesQueryBuilder from(Date from) {
        parameters.add(new FilesFromParameter(from));
        return this;
    }

    /**
     * Adds a filter for datetime to which objects will be returned.
     *
     * @param to A uploading datetime to which objects will be returned.
     */
    public FilesQueryBuilder to(Date to) {
        parameters.add(new FilesToParameter(to));
        return this;
    }

    @Override
    public Iterable<UploadcareFile> asIterable() {
        URI url = Urls.apiFiles();
        RequestHelper requestHelper = client.getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(client);
        return requestHelper
                .executePaginatedQuery(url, parameters, true, FilePageData.class, dataWrapper);
    }

    @Override
    public List<UploadcareFile> asList() {
        List<UploadcareFile> files = new ArrayList<>();
        for (UploadcareFile file : asIterable()) {
            files.add(file);
        }
        return files;
    }

    /**
     * Returns a limited amount of resources with specified offset Asynchronously.
     *
     * @param context  application context. @link android.content.Context
     * @param limit    amount of resources returned in callback.
     * @param next     amount of resources to skip. if {@code null} then no offset will be applied.
     * @param callback {@link UploadcareFilesCallback}.
     */
    public void asListAsync(Context context, int limit, URI next,
            UploadcareFilesCallback callback) {
        if (next == null) {
            parameters.add(new FilesLimitParameter(limit));
        }
        URI url = next == null ? Urls.apiFiles() : next;
        RequestHelper requestHelper = client.getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(client);
        requestHelper.executePaginatedQueryWithOffsetLimitAsync(context, url, parameters, true,
                dataWrapper,
                callback);
    }

    /**
     * Iterates through all resources and returns a complete list Asynchronously.
     *
     * @param callback {@link UploadcareAllFilesCallback}.
     */
    public void asListAsync(UploadcareAllFilesCallback callback) {
        new PaginatedQueryTask().execute(callback);
    }

    private class PaginatedQueryTask
            extends AsyncTask<UploadcareAllFilesCallback, Void, List<UploadcareFile>> {

        UploadcareAllFilesCallback callback;

        @Override
        protected List<UploadcareFile> doInBackground(UploadcareAllFilesCallback... callbacks) {
            callback = callbacks[0];
            try {
                return asList();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UploadcareFile> result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(
                        new UploadcareApiException("Unexpected error"));
            }
        }
    }
}