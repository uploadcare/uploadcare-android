package com.uploadcare.android.widget.controller;

import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.widget.BuildConfig;
import com.uploadcare.android.widget.activity.UploadcareActivity;
import com.uploadcare.android.widget.exceptions.UploadcareWidgetException;
import com.uploadcare.android.widget.interfaces.SocialApi;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class UploadcareWidget {

    private static UploadcareWidget mInstance = null;

    private String publicKey, privateKey = null;

    private RestAdapter restAdapter;

    private UploadcareClient mUploadcareClient;

    private SocialApi mSocialApi;

    private int style = -1;

    private boolean store = true;

    @FileType
    private String fileType= FILE_TYPE_ANY;

    private UploadcareFileCallback mCallback;
    //
    //Define the list of accepted constants
    @StringDef({SOCIAL_NETWORK_FACEBOOK, SOCIAL_NETWORK_INSTAGRAM, SOCIAL_NETWORK_VK,SOCIAL_NETWORK_BOX,
            SOCIAL_NETWORK_HUDDLE,SOCIAL_NETWORK_FLICKR,SOCIAL_NETWORK_EVERNOTE,SOCIAL_NETWORK_SKYDRIVE,SOCIAL_NETWORK_DROPBOX,SOCIAL_NETWORK_GDRIVE})

    //Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)

    //Declare the NavigationMode annotation
    public @interface SocialNetwork {}

    //Declare the constants
    public static final String SOCIAL_NETWORK_FACEBOOK = "facebook";

    public static final String SOCIAL_NETWORK_INSTAGRAM = "instagram";

    public static final String SOCIAL_NETWORK_VK = "vk";

    public static final String SOCIAL_NETWORK_BOX = "box";

    public static final String SOCIAL_NETWORK_HUDDLE = "huddle";

    public static final String SOCIAL_NETWORK_FLICKR = "flickr";

    public static final String SOCIAL_NETWORK_EVERNOTE = "evernote";

    public static final String SOCIAL_NETWORK_SKYDRIVE = "skydrive";

    public static final String SOCIAL_NETWORK_DROPBOX = "dropbox";

    public static final String SOCIAL_NETWORK_GDRIVE = "gdrive";

    @StringDef({FILE_TYPE_IMAGE, FILE_TYPE_VIDEO, FILE_TYPE_ANY})

    //Declare the NavigationMode annotation
    public @interface FileType {}

    public static final String FILE_TYPE_IMAGE = "image";
    public static final String FILE_TYPE_VIDEO = "video";
    public static final String FILE_TYPE_ANY = "";

    private UploadcareWidget() {

    }

    /**
     * Get instance of UploadcareWidget.
     * @return instance of UploadcareWidget
     */
    public static UploadcareWidget getInstance() {
        if (mInstance == null) {
            mInstance = new UploadcareWidget();
        }
        return mInstance;
    }

    /**
     * Initialize UploadcareWidget instance.
     * @param publicKey from Uploadcare dashboard.
     * @param privateKey from Uploadcare dashboard.
     */
    public void init(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.style = -1;
        this.mUploadcareClient = new UploadcareClient(publicKey,privateKey);
    }

    /**
     * Initialize UploadcareWidget instance with custom Style.
     * @param publicKey from Uploadcare dashboard.
     * @param privateKey from Uploadcare dashboard.
     * @param style Custom style for Uploadcare widget.
     */
    public void init(String publicKey, String privateKey, int style) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.style = style;
        this.mUploadcareClient = new UploadcareClient(publicKey,privateKey);
    }

    /**
     * Select and upload file to Uploadcare from any social network.
     *
     * @param context Context of the Activity that started file picker.
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public void selectFile(Context context, boolean storeUponUpload, UploadcareFileCallback callback){
        checkInit();
        this.mCallback = callback;
        this.fileType=FILE_TYPE_ANY;
        this.store=storeUponUpload;
        Intent intent = new Intent(context,
                UploadcareActivity.class);
        context.startActivity(intent);
    }

    /**
     * Select and upload specific file type to Uploadcare from any social network.
     *
     * @param context Context of the Activity that started file picker.
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     */
    public void selectFile(Context context, boolean storeUponUpload, @FileType String fileType, UploadcareFileCallback callback){
        checkInit();
        this.mCallback = callback;
        this.fileType=fileType;
        this.store=storeUponUpload;
        Intent intent = new Intent(context,
                UploadcareActivity.class);
        context.startActivity(intent);
    }

    /**
     *Select and upload file to Uploadcare from specified network.
     *
     * @param context Context of the Activity that started file picker.
     * @param network SocialNetwork
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     */
    public void selectFileFrom(Context context,@SocialNetwork String network, boolean storeUponUpload, UploadcareFileCallback callback){
        checkInit();
        this.mCallback = callback;
        this.fileType=FILE_TYPE_ANY;
        this.store=storeUponUpload;
        Intent intent = new Intent(context,
                UploadcareActivity.class);
        intent.putExtra("network", network);
        context.startActivity(intent);
    }

    /**
     * Select and upload specific file type to Uploadcare from specified network.
     *
     * @param context Context of the Activity that started file picker.
     * @param network SocialNetwork
     * @param callback {@link UploadcareFileCallback} will be called to notify about result or error.
     * @param storeUponUpload is set true - store the file upon uploading. Requires “automatic file storing” setting to be enabled.
     *              is set false - do not store file upon uploading.
     * @param fileType FileType
     */
    public void selectFileFrom(Context context,@SocialNetwork String network, @FileType String fileType, boolean storeUponUpload, UploadcareFileCallback callback){
        checkInit();
        this.mCallback = callback;
        this.fileType=fileType;
        this.store=storeUponUpload;
        Intent intent = new Intent(context,
                UploadcareActivity.class);
        intent.putExtra("network", network);
        context.startActivity(intent);
    }

    public UploadcareFileCallback getCallback(){
        checkInit();
        return mCallback;
    }

    public synchronized SocialApi getSocialApi() {
        checkInit();
        if (mSocialApi == null) {
            if (restAdapter == null) {
                RequestInterceptor requestInterceptor = new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("X-Uploadcare-PublicKey", publicKey);
                    }
                };
                restAdapter = new RestAdapter.Builder()
                        .setClient(new OkClient(mUploadcareClient.getHttpClient()))
                        .setEndpoint(BuildConfig.SOCIAL_API_ENDPOINT)
                        .setRequestInterceptor(requestInterceptor)
                        .build();
                restAdapter.setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE);

            }
            mSocialApi = restAdapter.create(SocialApi.class);
        }

        return mSocialApi;
    }

    private void checkInit(){
        if(publicKey==null){
            throw new UploadcareWidgetException("UploadcareWidget is not initialized. call init() before other methods.");
        }
    }

    public int getStyle(){
        return style;
    }

    /**
     * Get {@link UploadcareClient} for direct access to Uploadcare API.
     * @return UploadcareClient.
     */
    public UploadcareClient getUploadcareClient(){
        return mUploadcareClient;
    }

    public boolean storeUponUpload(){
        return store;
    }

    @FileType
    public String getFileType(){
        return fileType;
    }
}
