package com.uploadcare.android.example;

import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadFilesCallback;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.upload.FileUploader;
import com.uploadcare.android.library.upload.MultipleFilesUploader;
import com.uploadcare.android.library.upload.MultipleUploader;
import com.uploadcare.android.library.upload.Uploader;
import com.uploadcare.android.library.upload.UrlUploader;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ACTIVITY_CHOOSE_FILE = 0;

    View buttonsHolder;

    EditText urlEditText;

    TextView statusTextView;

    UploadcareClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonsHolder = findViewById(R.id.buttons_holder);
        urlEditText = (EditText) findViewById(R.id.from_url_edit_text);
        statusTextView = (TextView) findViewById(R.id.status);
        client = new UploadcareClient("0e9e279333e12535f844","3f06f5068b47f7591dea");//UploadcareClient.demoClient();
        findViewById(R.id.button_get_files).setOnClickListener(this);
        Button uploadFileButton = (Button) findViewById(R.id.button_upload_file);
        uploadFileButton.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            uploadFileButton
                    .setText(getResources().getString(R.string.activity_main_btn_upload_files));
        }

        findViewById(R.id.button_upload_url).setOnClickListener(this);
    }

    private void uploadFile(Context context, UploadcareClient client, Uri fileUri) {
        showProgressOrResult(true,
                getResources().getString(R.string.activity_main_status_uploading));
        Uploader uploader = new FileUploader(client, fileUri, context)
                .store(true);
        uploader.uploadAsync(new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                showProgressOrResult(false,
                        e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                showProgressOrResult(false,
                        file.toString());
            }
        });
    }

    private void uploadFiles(Context context, UploadcareClient client, List<Uri> filesUriList) {
        showProgressOrResult(true,
                getResources().getString(R.string.activity_main_status_uploading));
        MultipleUploader uploader = new MultipleFilesUploader(client, filesUriList, context).store(
                true);
        uploader.uploadAsync(new UploadFilesCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                showProgressOrResult(false,
                        e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(List<UploadcareFile> files) {
                StringBuilder resultStringBuilder = new StringBuilder();
                for(UploadcareFile file:files){
                    resultStringBuilder.append(file.toString()).append(
                            System.getProperty("line.separator")).append(System.getProperty("line.separator"));
                }
                showProgressOrResult(false,
                        resultStringBuilder.toString());
            }
        });
    }

    private void uploadFromUrl(UploadcareClient client, String sourceUrl) {
        showProgressOrResult(true,
                getResources().getString(R.string.activity_main_status_uploading));
        Uploader uploader = new UrlUploader(client, sourceUrl).store(true);
        uploader.uploadAsync(new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                showProgressOrResult(false,
                        e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                showProgressOrResult(false,
                        file.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_get_files:
                startGetFilesActivity();
                break;
            case R.id.button_upload_file:
                selectFileForUpload();
                break;
            case R.id.button_upload_url:
                if (checkUrl()) {
                    urlEditText.requestFocus(View.FOCUS_DOWN);
                    uploadFromUrl(client, urlEditText.getText().toString());
                }
                break;
        }
    }

    private void startGetFilesActivity() {
        startActivity(new Intent(this, FilesActivity.class));
    }

    private void selectFileForUpload() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent = Intent.createChooser(chooseFile,
                getResources().getString(R.string.activity_main_choose_file));
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_CHOOSE_FILE && resultCode == Activity.RESULT_OK) {
            if (data.getData() != null) {
                Uri fileUri = data.getData();
                uploadFile(this, client, fileUri);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    List<Uri> uriList = new ArrayList<>();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        Log.d("Files Fragment: ", clipData.getItemAt(i).getUri().getPath());
                        uriList.add(clipData.getItemAt(i).getUri());

                    }
                    uploadFiles(this, client, uriList);
                }
            }
        }
    }

    private void showProgressOrResult(boolean progress, String message) {
        if (progress) {
            buttonsHolder.setVisibility(View.GONE);
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(message);
        } else {
            buttonsHolder.setVisibility(View.VISIBLE);
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(message);
        }
    }

    private boolean checkUrl() {
        if (urlEditText.getText() != null && urlEditText.getText().length() > 0) {
            urlEditText.setError(null);
            return true;
        } else {
            urlEditText.setError(getResources().getString(R.string.activity_main_hint_upload_url));
            return false;
        }
    }

}
