package com.uploadcare.android.library.upload;

import com.uploadcare.android.library.api.RequestHelper;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadFilesCallback;
import com.uploadcare.android.library.data.UploadFromUrlData;
import com.uploadcare.android.library.data.UploadFromUrlStatusData;
import com.uploadcare.android.library.exceptions.UploadFailureException;
import com.uploadcare.android.library.urls.Urls;

import android.os.AsyncTask;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Uploadcare uploader for multiple URLs.
 */
public class MultipleUrlsUploader implements MultipleUploader {

    private final UploadcareClient client;

    private final List<String> sourceUrls;

    private String store = "auto";

    /**
     * Create a new uploader from a list of URLs.
     *
     * @param client     Uploadcare client
     * @param sourceUrls a list of URLs to upload from
     */
    public MultipleUrlsUploader(UploadcareClient client, List<String> sourceUrls) {
        this.client = client;
        this.sourceUrls = sourceUrls;
    }

    /**
     * Synchronously uploads the files to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     * Uploadcare is polled every 500 ms for upload progress.
     *
     * @return A list of Uploadcare files
     */
    public List<UploadcareFile> upload() throws UploadFailureException {
        return upload(500);
    }

    /**
     * Asynchronously uploads the list of files to Uploadcare.
     */
    public void uploadAsync(UploadFilesCallback callback) throws UploadFailureException {
        new MultipleUrlsUploadTask().execute(callback);
    }

    /**
     * Store the files upon uploading.
     *
     * @param store is set true - store the files upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store files upon uploading.
     */
    public MultipleUrlsUploader store(boolean store) {
        this.store = store ? String.valueOf(1) : String.valueOf(0);
        return this;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms
     * @return An Uploadcare file
     */
    public List<UploadcareFile> upload(int pollingInterval) throws UploadFailureException {
        if (sourceUrls == null) {
            throw new UploadFailureException();
        }
        List<UploadcareFile> results = new ArrayList<>();
        for (String sourceUrl : sourceUrls) {
            System.out.print("starting upload:" + sourceUrl);
            RequestHelper requestHelper = client.getRequestHelper();
            URI uploadUrl = Urls.uploadFromUrl(sourceUrl, client.getPublicKey(), store);
            String token = requestHelper
                    .executeQuery(RequestHelper.REQUEST_GET, uploadUrl.toString(), false,
                            UploadFromUrlData.class, null).token;
            URI statusUrl = Urls.uploadFromUrlStatus(token);
            while (true) {
                sleep(pollingInterval);
                UploadFromUrlStatusData data = requestHelper
                        .executeQuery(RequestHelper.REQUEST_GET, statusUrl.toString(), false,
                                UploadFromUrlStatusData.class, null);
                if (data.status.equals("success")) {
                    System.out.print(" upload status success:");
                    results.add(client.getFile(data.fileId));
                    break;
                } else if (data.status.equals("error") || data.status.equals("failed")) {
                    throw new UploadFailureException();
                }
            }
        }
        return results;
    }

    private class MultipleUrlsUploadTask
            extends AsyncTask<UploadFilesCallback, Void, List<UploadcareFile>> {

        UploadFilesCallback callback;

        @Override
        protected List<UploadcareFile> doInBackground(UploadFilesCallback... callbacks) {
            callback = callbacks[0];
            try {
                return upload(500);
            } catch (UploadFailureException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UploadcareFile> result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(new UploadFailureException());
            }
        }
    }
}
