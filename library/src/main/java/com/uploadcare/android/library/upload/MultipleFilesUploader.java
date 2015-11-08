package com.uploadcare.android.library.upload;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.uploadcare.android.library.api.RequestHelper;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadFilesCallback;
import com.uploadcare.android.library.data.UploadBaseData;
import com.uploadcare.android.library.exceptions.UploadFailureException;
import com.uploadcare.android.library.urls.Urls;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Uploadcare uploader for multiple files.
 */
public class MultipleFilesUploader implements MultipleUploader {

    private final UploadcareClient client;

    private final List<File> files;

    private final List<Uri> uris;

    private final Context context;

    private String store = "auto";

    /**
     * Creates a new uploader from a list of files on disk
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client Uploadcare client
     * @param files  Files on disk
     */
    public MultipleFilesUploader(UploadcareClient client, List<File> files) {
        this.client = client;
        this.files = files;
        this.uris = null;
        this.context = null;
    }

    /**
     * Creates a new uploader from a list of {@link android.net.Uri} objects references.
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client  Uploadcare client
     * @param uris    List of {@link android.net.Uri} objects references.
     * @param context Application context {@link android.content.Context}
     */
    public MultipleFilesUploader(UploadcareClient client, List<Uri> uris, Context context) {
        this.client = client;
        this.files = null;
        this.uris = uris;
        this.context = context;
    }

    /**
     * Synchronously uploads the files to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @return An list of Uploadcare files
     */
    public List<UploadcareFile> upload() throws UploadFailureException {
        URI uploadUrl = Urls.uploadBase();

        List<UploadcareFile> results = new ArrayList<>();

        if (files != null) {
            for (File file : files) {

                MultipartBuilder multipartBuilder = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("UPLOADCARE_PUB_KEY", client.getPublicKey())
                        .addFormDataPart("UPLOADCARE_STORE", store);

                multipartBuilder.addFormDataPart("file", file.getName(), RequestBody
                        .create(UploadUtils.getMimeType(file), file));

                RequestBody requestBody = multipartBuilder.build();

                String fileId = client.getRequestHelper()
                        .executeQuery(RequestHelper.REQUEST_POST, uploadUrl.toString(), false,
                                UploadBaseData.class, requestBody).file;
                results.add(client.getFile(fileId));
            }
        } else {
            for (Uri uri : uris) {

                MultipartBuilder multipartBuilder = new MultipartBuilder()
                        .type(MultipartBuilder.FORM)
                        .addFormDataPart("UPLOADCARE_PUB_KEY", client.getPublicKey())
                        .addFormDataPart("UPLOADCARE_STORE", store);

                InputStream iStream = null;
                try {
                    iStream = context.getContentResolver().openInputStream(uri);
                    byte[] inputData = UploadUtils.getBytes(iStream);
                    multipartBuilder.addFormDataPart("file", UploadUtils.getFileName(uri, context),
                            RequestBody.create(UploadUtils.MEDIA_TYPE_TEXT_PLAIN, inputData));
                } catch (IOException e) {
                    throw new UploadFailureException(e);
                }

                RequestBody requestBody = multipartBuilder.build();

                String fileId = client.getRequestHelper()
                        .executeQuery(RequestHelper.REQUEST_POST, uploadUrl.toString(), false,
                                UploadBaseData.class, requestBody).file;
                results.add(client.getFile(fileId));
            }
        }

        return results;
    }

    /**
     * Asynchronously uploads the list of files to Uploadcare.
     */
    public void uploadAsync(UploadFilesCallback callback) throws UploadFailureException {
        new MultipleUploadTask().execute(callback);
    }

    /**
     * Store the files upon uploading.
     *
     * @param store is set true - store the files upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store files upon uploading.
     */
    public MultipleFilesUploader store(boolean store) {
        this.store = store ? String.valueOf(1) : String.valueOf(0);
        return this;
    }

    private class MultipleUploadTask
            extends AsyncTask<UploadFilesCallback, Void, List<UploadcareFile>> {

        UploadFilesCallback callback;

        @Override
        protected List<UploadcareFile> doInBackground(UploadFilesCallback... callbacks) {
            callback = callbacks[0];
            try {
                return upload();
            } catch (Exception e) {
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
