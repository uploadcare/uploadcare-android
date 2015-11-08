package com.uploadcare.android.library.upload;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.uploadcare.android.library.api.RequestHelper;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
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

/**
 * Uploadcare uploader for files and binary data.
 */
public class FileUploader implements Uploader {

    private final UploadcareClient client;

    private final File file;

    private final byte[] bytes;

    private final String filename;

    private final Uri uri;

    private final String content;

    private final Context context;

    private String store = "auto";

    /**
     * Creates a new uploader from a file on disk
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client Uploadcare client
     * @param file   File on disk
     */
    public FileUploader(UploadcareClient client, File file) {
        this.client = client;
        this.file = file;
        this.bytes = null;
        this.filename = null;
        this.uri = null;
        this.context = null;
        this.content = null;
    }

    /**
     * Creates a new uploader from a {@link android.net.Uri} object reference.
     * (not to be confused with a file resource from Uploadcare API).
     *
     * @param client  Uploadcare client
     * @param uri     Object reference {@link android.net.Uri}.
     * @param context Application context {@link android.content.Context}.
     */
    public FileUploader(UploadcareClient client, Uri uri, Context context) {
        this.client = client;
        this.file = null;
        this.bytes = null;
        this.filename = null;
        this.uri = uri;
        this.context = context;
        this.content = null;
    }

    /**
     * Creates a new uploader from binary data.
     *
     * @param client   Uploadcare client
     * @param bytes    File contents as binary data
     * @param filename Original filename
     */
    public FileUploader(UploadcareClient client, byte[] bytes, String filename) {
        this.client = client;
        this.file = null;
        this.bytes = bytes;
        this.filename = filename;
        this.uri = null;
        this.context = null;
        this.content = null;
    }

    /**
     * Creates a new uploader from binary data.
     *
     * @param client  Uploadcare client
     * @param content Contents data as String object.
     */
    public FileUploader(UploadcareClient client, String content) {
        this.client = client;
        this.file = null;
        this.bytes = null;
        this.filename = null;
        this.uri = null;
        this.context = null;
        this.content = content;
    }

    /**
     * Synchronously uploads the file to Uploadcare.
     *
     * The calling thread will be busy until the upload is finished.
     *
     * @return An Uploadcare file
     */
    public UploadcareFile upload() throws UploadFailureException {
        URI uploadUrl = Urls.uploadBase();

        MultipartBuilder multipartBuilder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("UPLOADCARE_PUB_KEY", client.getPublicKey())
                .addFormDataPart("UPLOADCARE_STORE", store);

        if (file != null) {
            multipartBuilder.addFormDataPart("file", file.getName(),
                    RequestBody.create(UploadUtils.getMimeType(
                            file), file));
        } else if (uri != null) {
            InputStream iStream = null;
            try {
                iStream = context.getContentResolver().openInputStream(uri);
                byte[] inputData = UploadUtils.getBytes(iStream);
                multipartBuilder.addFormDataPart("file", UploadUtils.getFileName(uri, context),
                        RequestBody.create(UploadUtils.MEDIA_TYPE_TEXT_PLAIN, inputData));
            } catch (IOException e) {
                throw new UploadFailureException(e);
            }
        } else if (content != null) {
            multipartBuilder.addFormDataPart("file", content);
        } else {
            multipartBuilder.addFormDataPart("file", filename,
                    RequestBody.create(UploadUtils.MEDIA_TYPE_TEXT_PLAIN, bytes));
        }

        RequestBody requestBody = multipartBuilder.build();

        String fileId = client.getRequestHelper()
                .executeQuery(RequestHelper.REQUEST_POST, uploadUrl.toString(), false,
                        UploadBaseData.class, requestBody).file;
        System.out.println("uploaded file id:" + fileId);
        return client.getFile(fileId);
    }

    /**
     * Asynchronously uploads the file to Uploadcare.
     *
     * @param callback callback {@link UploadcareFileCallback}
     */
    public void uploadAsync(UploadcareFileCallback callback) throws UploadFailureException {
        new UploadTask().execute(callback);
    }

    /**
     * Store the file upon uploading.
     *
     * @param store is set true - store the file upon uploading. Requires “automatic file storing”
     *              setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public FileUploader store(boolean store) {
        this.store = store ? String.valueOf(1) : String.valueOf(0);
        return this;
    }

    private class UploadTask
            extends AsyncTask<UploadcareFileCallback, Void, UploadcareFile> {

        UploadcareFileCallback callback;

        @Override
        protected UploadcareFile doInBackground(UploadcareFileCallback... callbacks) {
            callback = callbacks[0];
            try {
                return upload();
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