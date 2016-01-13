package com.uploadcare.android.widget.activity;

import com.uploadcare.android.widget.BuildConfig;
import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.controller.UploadcareWidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class UploadcareAuthActivity extends AppCompatActivity {

    public static final int RESULT_ERROR = 10;

    private WebView mWebView;

    private CircularProgressBar mCircularProgressBar;

    private String loginUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(UploadcareWidget.getInstance().getStyle()!=-1){
            getTheme().applyStyle(UploadcareWidget.getInstance().getStyle(), true);
        }else {
            getTheme().applyStyle(R.style.UploadcareStyle, true);
        }
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(!UploadcareWidget.getInstance().isInited())finish();
        setContentView(R.layout.ucw_activity_auth);
        mWebView = (WebView) findViewById(R.id.ucw_webview);
        mCircularProgressBar = (CircularProgressBar) findViewById(R.id.ucw_progress);
        if (savedInstanceState != null) {
            loginUrl = savedInstanceState.getString("loginUrl");
        } else {
            Bundle extras = getIntent().getExtras();
            loginUrl = extras.getString("loginUrl");
        }
        setResult(RESULT_CANCELED);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new UploadcareWebViewClient());
        mWebView.loadUrl(loginUrl);
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

    private class UploadcareWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mCircularProgressBar.setVisibility(View.GONE);
            if (url.startsWith(BuildConfig.SOCIAL_API_ENDPOINT)) {
                String cookies = CookieManager.getInstance().getCookie(url);
                if (cookies != null && cookies.length() > 0) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("cookie", cookies);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_ERROR);
                }
                finish();
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                WebResourceError error) {
            super.onReceivedError(view, request, error);
            mCircularProgressBar.setVisibility(View.GONE);
            setResult(RESULT_ERROR);
            finish();
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request,
                WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (loginUrl != null) {
            outState.putString("loginUrl", loginUrl);
        }
    }
}
