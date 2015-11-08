package com.uploadcare.android.example;

import com.squareup.picasso.Picasso;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.urls.CdnPathBuilder;
import com.uploadcare.android.library.urls.Urls;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.net.URI;

/**
 * Activity showcase different CdnPathBuilder options for image files.
 */
public class CdnActivity extends AppCompatActivity {

    private View mRootView;

    UploadcareClient client;

    String fileId;

    /**
     * Initialize variables and creates {@link UploadcareClient}. Gets UploadcareFile asynchronously
     * by fileId.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_cdn);
        mRootView = findViewById(R.id.root);
        client = UploadcareClient.demoClient();
        fileId = getIntent().getExtras().getString("fileId");

        client.getFileAsync(this, fileId, new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                Snackbar.make(mRootView, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                loadImages(file);
            }
        });
    }

    /**
     * Populates views. Generates different CDN urls for various effects and loads images to views.
     */
    private void loadImages(UploadcareFile file) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        for (int i = 0; i < 7; i++) {
            CdnPathBuilder builder = file.cdnPath();
            builder.resizeWidth(width);
            switch (i) {
                case 0:
                    URI url = Urls.cdn(builder);
                    Picasso.with(this).load(url.toString())
                            .into((ImageView) findViewById(R.id.cdn1));
                    break;
                case 1:
                    builder.grayscale();
                    URI url2 = Urls.cdn(builder);
                    Picasso.with(this).load(url2.toString())
                            .into((ImageView) findViewById(R.id.cdn2));
                    break;
                case 2:
                    builder.flip();
                    URI url3 = Urls.cdn(builder);
                    Picasso.with(this).load(url3.toString())
                            .into((ImageView) findViewById(R.id.cdn3));
                    break;
                case 3:
                    builder.invert();
                    URI url4 = Urls.cdn(builder);
                    Picasso.with(this).load(url4.toString())
                            .into((ImageView) findViewById(R.id.cdn4));
                    break;
                case 4:
                    builder.mirror();
                    URI url5 = Urls.cdn(builder);
                    Picasso.with(this).load(url5.toString())
                            .into((ImageView) findViewById(R.id.cdn5));
                    break;
                case 5:
                    builder.blur(100);
                    URI url6 = Urls.cdn(builder);
                    Picasso.with(this).load(url6.toString())
                            .into((ImageView) findViewById(R.id.cdn6));
                    break;
                case 6:
                    builder.sharp(10);
                    URI url7 = Urls.cdn(builder);
                    Picasso.with(this).load(url7.toString())
                            .into((ImageView) findViewById(R.id.cdn7));
                    break;
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
