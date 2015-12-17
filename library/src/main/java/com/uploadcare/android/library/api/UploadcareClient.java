package com.uploadcare.android.library.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.uploadcare.android.library.callbacks.CopyFileCallback;
import com.uploadcare.android.library.callbacks.ProjectCallback;
import com.uploadcare.android.library.callbacks.RequestCallback;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.callbacks.UploadcareGroupCallback;
import com.uploadcare.android.library.data.CopyFileData;
import com.uploadcare.android.library.data.FileData;
import com.uploadcare.android.library.data.GroupData;
import com.uploadcare.android.library.data.ProjectData;
import com.uploadcare.android.library.urls.Urls;

import android.content.Context;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class UploadcareClient {

    private final String publicKey;

    private final String privateKey;

    private final boolean simpleAuth;

    private final OkHttpClient httpClient;

    private final ObjectMapper objectMapper;

    private final RequestHelperProvider requestHelperProvider;

    /**
     * Initializes a client with custom access keys and simple authentication.
     *
     * @param publicKey  Public key
     * @param privateKey Private key
     */
    public UploadcareClient(String publicKey, String privateKey) {
        this(publicKey, privateKey, true);
    }

    /**
     * Initializes a client with custom access keys.
     * Can use simple or secure authentication.
     *
     * @param publicKey  Public key
     * @param privateKey Private key
     * @param simpleAuth If {@code false}, HMAC-based authentication is used
     */
    public UploadcareClient(
            String publicKey,
            String privateKey,
            boolean simpleAuth) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.simpleAuth = simpleAuth;

        this.requestHelperProvider = new DefaultRequestHelperProvider();
        httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);

        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(
                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Creates a client with demo credentials.
     * Useful for tests and anonymous access.
     *
     * <b>Warning!</b> Do not use in production.
     * All demo account files are eventually purged.
     *
     * @return A demo client
     */
    public static UploadcareClient demoClient() {
        return new UploadcareClient("demopublickey", "demoprivatekey");
    }

    /**
     * Returns the public key.
     *
     * @return Public key
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * Returns the private key.
     *
     * @return Private key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Returns {@code true}, if simple authentication is used.
     *
     * @return {@code true}, if simple authentication is used, {@code false} otherwise
     */
    public boolean isSimpleAuth() {
        return simpleAuth;
    }

    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public RequestHelper getRequestHelper() {
        return requestHelperProvider.get(this);
    }

    /**
     * Requests project info from the API.
     *
     * @return Project resource
     */
    public Project getProject() {
        URI url = Urls.apiProject();
        RequestHelper requestHelper = getRequestHelper();

        ProjectData projectData = requestHelper.executeQuery(RequestHelper.REQUEST_GET,
                url.toString(), true, ProjectData.class, null);
        return new Project(this, projectData);
    }

    /**
     * Requests project info from the API Asynchronously.
     *
     * @param context  Application context. {@link android.content.Context}
     * @param callback callback  {@link ProjectCallback} with either
     *                 an Project response or a failure exception.
     */
    public void getProjectAsync(Context context, ProjectCallback callback) {
        URI url = Urls.apiProject();
        RequestHelper requestHelper = getRequestHelper();
        ProjectDataWrapper dataWrapper = new ProjectDataWrapper(this);
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                ProjectData.class, dataWrapper, callback, null);
    }

    /**
     * Requests file data.
     *
     * @param fileId Resource UUID
     * @return UploadcareFile resource
     */
    public UploadcareFile getFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        FileData fileData = requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, FileData.class, null);
        return new UploadcareFile(this, fileData);
    }

    /**
     * Requests file data Asynchronously.
     *
     * @param context  Application context. {@link android.content.Context }
     * @param fileId   Resource UUID
     * @param callback callback  {@link UploadcareFileCallback} with either
     *                 an UploadcareFile response or a failure exception.
     */
    public void getFileAsync(Context context, String fileId, UploadcareFileCallback callback) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        FileDataWrapper dataWrapper = new FileDataWrapper(this);
        requestHelper
                .executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                        FileData.class,
                        dataWrapper, callback, null);
    }

    /**
     * Requests group data.
     *
     * @param groupId Group ID
     * @return UploadcareGroup resource
     */
    public UploadcareGroup getGroup(String groupId) {
        URI url = Urls.apiGroup(groupId);
        RequestHelper requestHelper = getRequestHelper();
        GroupData groupData = requestHelper.executeQuery(RequestHelper.REQUEST_GET, url.toString(),
                true, GroupData.class, null);
        return new UploadcareGroup(this, groupData);
    }

    /**
     * Requests file data Asynchronously.
     *
     * @param context  Application context. {@link android.content.Context }
     * @param groupId  Group ID
     * @param callback callback  {@link UploadcareGroupCallback} with either
     *                 an UploadcareFile response or a failure exception.
     */
    public void getGroupAsync(Context context, String groupId, UploadcareGroupCallback callback) {
        URI url = Urls.apiGroup(groupId);
        RequestHelper requestHelper = getRequestHelper();
        GroupDataWrapper dataWrapper = new GroupDataWrapper(this);
        requestHelper
                .executeQueryAsync(context, RequestHelper.REQUEST_GET, url.toString(), true,
                        GroupData.class,
                        dataWrapper, callback, null);
    }

    /**
     * Mark all files in a group as stored (available on CDN).
     *
     * @param groupId Group ID
     */
    public void storeGroup(String groupId) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(""), groupId);
        URI url = Urls.apiGroupStorage(groupId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(RequestHelper.REQUEST_PUT, url.toString(), true, requestBody);
    }

    /**
     * Mark all files in a group as stored (available on CDN). Asynchronously.
     *
     * @param context  Application context. {@link android.content.Context }
     * @param groupId  Group ID
     * @param callback callback  {@link RequestCallback} with either
     *                 an HTTP response or a failure exception.
     */
    public void storeGroupAsync(Context context, String groupId, RequestCallback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(""), groupId);
        URI url = Urls.apiGroupStorage(groupId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_PUT, url.toString(),
                true, callback,
                requestBody);
    }

    /**
     * Begins to build a request for uploaded files for the current account.
     *
     * @return UploadcareFile resource request builder
     */
    public FilesQueryBuilder getFiles() {
        return new FilesQueryBuilder(this);
    }

    /**
     * Begins to build a request for groups for the current account.
     *
     * @return Group resource request builder
     */
    public GroupsQueryBuilder getGroups() {
        return new GroupsQueryBuilder(this);
    }

    /**
     * Marks a file as deleted.
     *
     * @param fileId Resource UUID
     */
    public void deleteFile(String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(RequestHelper.REQUEST_DELETE, url.toString(), true, null);
    }

    /**
     * Marks a file as deleted Asynchronously.
     *
     * @param context Application context. {@link android.content.Context }
     * @param fileId  Resource UUID
     */
    public void deleteFileAsync(Context context, String fileId) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_DELETE, url.toString(),
                true, null,
                null);
    }

    /**
     * Marks a file as deleted Asynchronously.
     *
     * @param context  Application context. {@link android.content.Context }
     * @param fileId   Resource UUID
     * @param callback callback  {@link RequestCallback} with either
     *                 an HTTP response or a failure exception.
     */
    public void deleteFileAsync(Context context, String fileId, RequestCallback callback) {
        URI url = Urls.apiFile(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper
                .executeCommandAsync(context, RequestHelper.REQUEST_DELETE, url.toString(), true,
                        callback,
                        null);
    }

    /**
     * Marks a file as saved.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param fileId Resource UUID
     */
    public void saveFile(String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommand(RequestHelper.REQUEST_POST, url.toString(), true, null);
    }

    /**
     * Marks a file as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context Application context. {@link android.content.Context }
     * @param fileId  Resource UUID
     */
    public void saveFileAsync(Context context, String fileId) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper
                .executeCommandAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                        null, null);
    }

    /**
     * Marks a file as saved Asynchronously.
     *
     * This has to be done for all files you want to keep.
     * Unsaved files are eventually purged.
     *
     * @param context  Application context. @link android.content.Context
     * @param fileId   Resource UUID
     * @param callback callback  {@link RequestCallback} with either
     *                 an HTTP response or a failure exception.
     */
    public void saveFileAsync(Context context, String fileId, RequestCallback callback) {
        URI url = Urls.apiFileStorage(fileId);
        RequestHelper requestHelper = getRequestHelper();
        requestHelper.executeCommandAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                callback, null);
    }

    /**
     * @param fileId  Resource UUID
     * @param storage Target storage name
     * @return An object containing the results of the copy request
     */
    public CopyFileData copyFile(String fileId, String storage) {
        RequestHelper requestHelper = getRequestHelper();
        URI url = Urls.apiFiles();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder()
                .add("source", fileId);
        if (storage != null && !storage.isEmpty()) {
            formEncodingBuilder.add("target", storage);
        }
        RequestBody formBody = formEncodingBuilder.build();
        return requestHelper
                .executeQuery(RequestHelper.REQUEST_POST, url.toString(), true, CopyFileData.class,
                        formBody);
    }

    /**
     * @param context  Application context. @link android.content.Context
     * @param fileId   Resource UUID
     * @param storage  Target storage name
     * @param callback callback  {@link CopyFileCallback} with either
     *                 an CopyFileData response or a failure exception.
     */
    public void copyFileAsync(Context context, String fileId, String storage,
            CopyFileCallback callback) {
        RequestHelper requestHelper = getRequestHelper();
        URI url = Urls.apiFiles();

        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder()
                .add("source", fileId);
        if (storage != null && !storage.isEmpty()) {
            formEncodingBuilder.add("target", storage);
        }
        RequestBody formBody = formEncodingBuilder.build();
        requestHelper.executeQueryAsync(context, RequestHelper.REQUEST_POST, url.toString(), true,
                CopyFileData.class, null, callback, formBody);
    }
}