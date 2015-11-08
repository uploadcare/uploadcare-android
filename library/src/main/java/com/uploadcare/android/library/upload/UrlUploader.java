package com.uploadcare.android.library.upload;

import com.uploadcare.android.library.api.RequestHelper;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.data.UploadFromUrlData;
import com.uploadcare.android.library.data.UploadFromUrlStatusData;
import com.uploadcare.android.library.exceptions.UploadFailureException;
import com.uploadcare.android.library.urls.Urls;

import android.os.AsyncTask;

import java.net.URI;

/**
 * Uploadcare uploader for URLs.
 */
public class UrlUploader implements Uploader {

    private final UploadcareClient client;

    private final String sourceUrl;

    private String store = "auto";

    /**
     * Create a new uploader from a URL.
     *
     * @param client    Uploadcare client
     * @param sourceUrl URL to upload from
     */
    public UrlUploader(UploadcareClient client, String sourceUrl) {
        this.client = client;
        this.sourceUrl = sourceUrl;
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     * Uploadcare is polled every 500 ms for upload progress.
     *
     * @return An Uploadcare file
     */
    public UploadcareFile upload() throws UploadFailureException {
        return upload(500);
    }


    public void uploadAsync(UploadcareFileCallback callback) throws UploadFailureException {
        new UrlUploadTask().execute(callback);
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @param pollingInterval Progress polling interval in ms
     * @return An Uploadcare file
     *
     * @throws {@link UploadFailureException}
     */
    public UploadcareFile upload(int pollingInterval) throws UploadFailureException {

        RequestHelper requestHelper = client.getRequestHelper();
        URI uploadUrl = Urls.uploadFromUrl(sourceUrl, client.getPublicKey(),store);
        String token = requestHelper
                .executeQuery(RequestHelper.REQUEST_GET,uploadUrl.toString(), false, UploadFromUrlData.class,null).token;
        URI statusUrl = Urls.uploadFromUrlStatus(token);
        System.out.println("url upload request done");
        while (true) {
            sleep(pollingInterval);
            UploadFromUrlStatusData data = requestHelper
                    .executeQuery(RequestHelper.REQUEST_GET,statusUrl.toString(), false, UploadFromUrlStatusData.class,null);
            if (data.status.equals("success")) {
                System.out.println("url upload request status success");
                return client.getFile(data.fileId);
            } else if (data.status.equals("error") || data.status.equals("failed")) {
                System.out.println("url upload request status fail");
                throw new UploadFailureException(data.status);
            }
        }
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public UrlUploader store(boolean store) {
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

    private class UrlUploadTask
            extends AsyncTask<UploadcareFileCallback, Void, UploadcareFile> {

        UploadcareFileCallback callback;

        @Override
        protected UploadcareFile doInBackground(UploadcareFileCallback... callbacks) {
            callback = callbacks[0];
            try {
                return upload(500);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(UploadcareFile result) {
            if (result != null) {
                callback.onSuccess(result);
            } else {
                callback.onFailure(new UploadFailureException());
            }
        }
    }
}