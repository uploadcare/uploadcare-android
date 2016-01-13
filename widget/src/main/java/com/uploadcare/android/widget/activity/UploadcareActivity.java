package com.uploadcare.android.widget.activity;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.upload.FileUploader;
import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.adapter.SocialNetworksAdapter;
import com.uploadcare.android.widget.controller.UploadcareWidget;
import com.uploadcare.android.widget.data.SocialSource;
import com.uploadcare.android.widget.data.SocialSourcesResponse;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UploadcareActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    private static final int CHOOSE_FILE_ACTIVITY_REQUEST_CODE = 300;

    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final int MEDIA_TYPE_VIDEO = 2;

    private SocialSourcesResponse mSocialSources;

    private String network = null;

    private Uri tempFileUri = null;

    /**
     * Initialize Activity and variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (UploadcareWidget.getInstance().getStyle() != -1) {
            getTheme().applyStyle(UploadcareWidget.getInstance().getStyle(), true);
        } else {
            getTheme().applyStyle(R.style.UploadcareStyle, true);
        }
        super.onCreate(savedInstanceState);
        if(!UploadcareWidget.getInstance().isInited())finish();
        setContentView(R.layout.ucw_activity_uploadcare);

        if (savedInstanceState != null) {
            mSocialSources = savedInstanceState.getParcelable("socialsources");
            network = savedInstanceState.getString("network", null);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                network = extras.getString("network", null);
                tempFileUri = extras.getParcelable("tempfile");
            }
        }
        if (mSocialSources != null) {
            showNetworks();
        } else {
            getAvailableNetworks();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSocialSources != null) {
            outState.putParcelable("socialsource", mSocialSources);
        }
        if (network != null) {
            outState.putString("network", network);
        }
        if (tempFileUri != null) {
            outState.putParcelable("tempfile", tempFileUri);
        }
    }

    private void getAvailableNetworks() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.UploadcareWidget_AlertDialogStyle);
        builder.setView(R.layout.ucw_progress_bar);
        builder.setTitle(R.string.ucw_action_loading_networks);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        UploadcareWidget.getInstance().getSocialApi().getSources(
                new Callback<SocialSourcesResponse>() {
                    @Override
                    public void success(SocialSourcesResponse socialSourcesResponse,
                            Response response) {
                        mSocialSources = socialSourcesResponse;
                        dialog.dismiss();
                        showNetworks();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.dismiss();
                        finish();
                        UploadcareWidget.getInstance().getCallback()
                                .onFailure(new UploadcareApiException(error.getLocalizedMessage()));
                    }
                });
    }

    private void showNetworks() {
        if (network == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    R.style.UploadcareWidget_AlertDialogStyle);
            builder.setTitle(R.string.ucw_action_select_network);
            builder.setCancelable(true);
            final SocialNetworksAdapter adapter = new SocialNetworksAdapter(this,
                    mSocialSources.sources);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton(
                    R.string.ucw_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            builder.setAdapter(
                    adapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SocialSource socialSource = adapter.getItem(which);
                            launchNetwork(socialSource);
                        }
                    });
            final AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            SocialSource socialSource = null;
            for (SocialSource source : mSocialSources.sources) {
                if (source.name.equalsIgnoreCase(network)) {
                    socialSource = source;
                    break;
                }
            }
            Intent intent = new Intent(UploadcareActivity.this,
                    UploadcareFilesActivity.class);
            intent.putExtra("socialsource", socialSource);
            startActivity(intent);
            finish();
        }
    }

    private void launchNetwork(SocialSource socialSource) {
        switch (socialSource.name) {
            case UploadcareWidget.SOCIAL_NETWORK_CAMERA:
                tempFileUri = null;
                // create Intent to take a picture and return control to the calling application
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                tempFileUri = getOutputMediaFileUri(
                        MEDIA_TYPE_IMAGE); // create a file to save the image
                cameraIntent
                        .putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri); // set the image file name

                // start the image capture Intent
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case UploadcareWidget.SOCIAL_NETWORK_VIDEOCAM:
                tempFileUri = null;
                //create new Intent
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                tempFileUri = getOutputMediaFileUri(
                        MEDIA_TYPE_VIDEO);  // create a file to save the video
                videoIntent
                        .putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);  // set the image file name

                videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
                        1); // set the video image quality to high

                // start the Video Capture Intent
                startActivityForResult(videoIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
                break;
            case UploadcareWidget.SOCIAL_NETWORK_FILE:
                Intent chooseFile;
                Intent intentFile;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType(getTypeForFileChooser());
                chooseFile.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intentFile = Intent.createChooser(chooseFile,
                        getResources().getString(R.string.ucw_choose_file));
                startActivityForResult(intentFile, CHOOSE_FILE_ACTIVITY_REQUEST_CODE);
                break;
            default:
                Intent intent = new Intent(UploadcareActivity.this,
                        UploadcareFilesActivity.class);
                intent.putExtra("socialsource", socialSource);
                startActivity(intent);
                finish();
                break;
        }
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "Cache");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                uploadFile(tempFileUri);
            } else {
                // User cancelled the image capture
                finish();
            }
        } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                uploadFile(tempFileUri);
            } else {
                // User cancelled the video capture
                finish();
            }
        } else if (requestCode == CHOOSE_FILE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Uri fileUri = data.getData();
                uploadFile(fileUri);
            } else {
                finish();
            }
        }
    }

    private void uploadFile(Uri fileUri) {
        if (fileUri == null) {
            finish();
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.UploadcareWidget_AlertDialogStyle);
        builder.setView(R.layout.ucw_progress_bar);
        builder.setTitle(R.string.ucw_action_loading_image);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();

        FileUploader uploader = new FileUploader(
                UploadcareWidget.getInstance().getUploadcareClient(), fileUri, this)
                .store(UploadcareWidget.getInstance().storeUponUpload());
        uploader.uploadAsync(new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                dialog.dismiss();
                finish();
                UploadcareWidget.getInstance().getCallback().onFailure(e);
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                dialog.dismiss();
                finish();
                UploadcareWidget.getInstance().getCallback().onSuccess(file);
            }
        });
    }

    private String getTypeForFileChooser() {
        String fileType = UploadcareWidget.getInstance().getFileType();
        switch (fileType) {
            case UploadcareWidget.FILE_TYPE_IMAGE:
                return "image/*";
            case UploadcareWidget.FILE_TYPE_VIDEO:
                return "video/*";
            default:
                return "*/*";
        }
    }
}
