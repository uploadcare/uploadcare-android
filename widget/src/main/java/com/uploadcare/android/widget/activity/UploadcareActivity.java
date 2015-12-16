package com.uploadcare.android.widget.activity;

import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.adapter.SocialNetworksAdapter;
import com.uploadcare.android.widget.controller.UploadcareWidget;
import com.uploadcare.android.widget.data.SocialSource;
import com.uploadcare.android.widget.data.SocialSourcesResponse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UploadcareActivity extends AppCompatActivity {

    private SocialSourcesResponse mSocialSources;

    private String network = null;

    /**
     * Initialize Activity and variables
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(UploadcareWidget.getInstance().getStyle()!=-1){
            getTheme().applyStyle(UploadcareWidget.getInstance().getStyle(), true);
        }else {
            getTheme().applyStyle(R.style.UploadcareStyle, true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ucw_activity_uploadcare);

        if (savedInstanceState != null) {
            mSocialSources = savedInstanceState.getParcelable("socialsources");
            network = savedInstanceState.getString("network", null);
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                network = extras.getString("network", null);
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
                        UploadcareWidget.getInstance().getCallback().onFailure(new UploadcareApiException(error.getLocalizedMessage()));
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
                            Intent intent = new Intent(UploadcareActivity.this,
                                    UploadcareFilesActivity.class);
                            intent.putExtra("socialsource", socialSource);
                            startActivity(intent);
                            finish();
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
}
