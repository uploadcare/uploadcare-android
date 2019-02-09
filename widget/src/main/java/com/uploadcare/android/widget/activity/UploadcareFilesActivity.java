package com.uploadcare.android.widget.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.upload.UrlUploader;
import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.adapter.ToolbarSpinnerAdapter;
import com.uploadcare.android.widget.controller.UploadcareWidget;
import com.uploadcare.android.widget.data.Chunk;
import com.uploadcare.android.widget.data.ChunkResponse;
import com.uploadcare.android.widget.data.SelectedFile;
import com.uploadcare.android.widget.data.SocialSource;
import com.uploadcare.android.widget.fragment.UploadcareFilesFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadcareFilesActivity extends AppCompatActivity implements UploadcareFilesFragment.OnFileActionsListener,AdapterView.OnItemSelectedListener{

    private static final int REQUEST_AUTH=1;

    private SocialSource mSocialSource;

    private CoordinatorLayout mCoordinatorLayout;

    private Spinner mSpinner;

    private static final String fragmentTag="latest";

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
        if(!UploadcareWidget.getInstance().isInited())finish();
        setContentView(R.layout.ucw_activity_files);
        if (savedInstanceState != null) {
            mSocialSource = savedInstanceState.getParcelable("socialsource");
        } else {
            Bundle extras = getIntent().getExtras();
            mSocialSource=extras.getParcelable("socialsource");
        }
        prepareUI();
        if (savedInstanceState == null) {
            UploadcareFilesFragment filesFragment = UploadcareFilesFragment.newInstance(mSocialSource,mSocialSource.rootChunks,"",true);
            getSupportFragmentManager().beginTransaction().add(R.id.ucw_fragment_holder,
                    filesFragment, fragmentTag).commit();
        }
    }



    private void needAuthorization(ChunkResponse chunkResponse){
        Intent intent = new Intent(UploadcareFilesActivity.this,
                UploadcareAuthActivity.class);
        intent.putExtra("loginUrl", chunkResponse.loginLink);
        startActivityForResult(intent, REQUEST_AUTH);
    }

    private void prepareUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.ucw_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mSpinner = (Spinner) findViewById(R.id.ucw_spinner);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.ucw_coordinator_layout);
        ToolbarSpinnerAdapter toolbarSpinnerAdapter = new ToolbarSpinnerAdapter(this,
                mSocialSource.rootChunks);
        mSpinner.setAdapter(toolbarSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setSelection(0);

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        UploadcareFilesFragment filesFragment
                                = (UploadcareFilesFragment) getSupportFragmentManager()
                                .findFragmentByTag(
                                        fragmentTag);
                        if (filesFragment != null && filesFragment.isAdded()) {
                            Log.d("UploadcareFilesActivity",
                                    "onBackStackChanged title:" + filesFragment.getTitle());
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(filesFragment.getTitle());
                            }
                        }

                        Log.d("UploadcareFilesActivity", "onBackStackChanged");
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            mSpinner.setVisibility(View.GONE);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayShowTitleEnabled(true);
                            }
                        } else {
                            mSpinner.setVisibility(View.VISIBLE);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayShowTitleEnabled(false);
                            }
                        }
                    }
                });
    }

    private void updateChunk(int position,List<Chunk> chunks,String title){
        Log.d("UploadcareFilesActivity", "updateChunk");
        if(chunks==null) {
            UploadcareFilesFragment filesFragment
                    = (UploadcareFilesFragment) getSupportFragmentManager().findFragmentByTag(
                    fragmentTag);
            if (filesFragment != null && filesFragment.isAdded()) {
                filesFragment.changeChunk(position);
            }
        }else{
            UploadcareFilesFragment filesFragment = UploadcareFilesFragment.newInstance(mSocialSource,chunks,title,false);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.ucw_fragment_slide_left_enter,
                    0,
                    0,
                    R.anim.ucw_fragment_slide_right_exit).add(R.id.ucw_fragment_holder,
                    filesFragment, fragmentTag).addToBackStack(null).commit();
        }
    }

    private void refreshChunk(){
        UploadcareFilesFragment filesFragment
                = (UploadcareFilesFragment) getSupportFragmentManager().findFragmentByTag(
                fragmentTag);
        if (filesFragment != null && filesFragment.isAdded()) {
            filesFragment.refreshChunk();
        }
    }

    private void openChunk(List<Chunk> chunks,String title){
        updateChunk(0, chunks,title);
    }

    private void checkBackStack(){
        if(getSupportFragmentManager().getBackStackEntryCount()>0){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_AUTH) {
            if(!UploadcareWidget.getInstance().isInited())finish();
            if (resultCode == RESULT_OK) {
                // The Intent's data contains cookie.
                Bundle extras = data.getExtras();
                String cookie = extras.getString("cookie");
                mSocialSource.saveCookie(UploadcareFilesActivity.this, cookie);
                refreshChunk();
            }else if(resultCode== UploadcareAuthActivity.RESULT_ERROR){
                finish();
                UploadcareWidget.getInstance().getCallback().onFailure(
                        new UploadcareApiException(getResources().getString(R.string.ucw_error_auth)));
            }else {
                finish();
            }
        }
    }

    private void showError(String message){
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void signOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.UploadcareWidget_AlertDialogStyle);
        builder.setView(R.layout.ucw_progress_bar);
        builder.setTitle(R.string.ucw_action_signout);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        UploadcareWidget
                .getInstance()
                .getSocialApi()
                .signOut(mSocialSource.getCookie(this), mSocialSource.urls.session)
                .enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, Response<Response> response) {
                        dialog.dismiss();
                        mSocialSource.deleteCookie(UploadcareFilesActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {
                        dialog.dismiss();
                        showError(t.getLocalizedMessage());
                    }
                });
    }

    private void selectFile(String fileUrl) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                R.style.UploadcareWidget_AlertDialogStyle);
        builder.setView(R.layout.ucw_progress_bar);
        builder.setTitle(R.string.ucw_action_loading_image);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        UploadcareWidget
                .getInstance()
                .getSocialApi()
                .selectFile(mSocialSource.getCookie(this), mSocialSource.urls.done, fileUrl)
                .enqueue(new Callback<SelectedFile>() {
                    @Override
                    public void onResponse(Call<SelectedFile> call, Response<SelectedFile> response) {
                        uploadFileFromUrl(dialog, response.body());
                    }

                    @Override
                    public void onFailure(Call<SelectedFile> call, Throwable t) {
                        dialog.dismiss();
                        showError(t.getLocalizedMessage());
                    }
                });
    }

    private void uploadFileFromUrl(final AlertDialog dialog,SelectedFile file){
        UrlUploader uploader = new UrlUploader(UploadcareWidget.getInstance().getUploadcareClient(), file.url).store(UploadcareWidget.getInstance().storeUponUpload());
        uploader.uploadAsync(new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                dialog.dismiss();
                showError(e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                dialog.dismiss();
                finish();
                UploadcareWidget.getInstance().getCallback().onSuccess(file);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mSocialSource!=null){
            outState.putParcelable("socialsource",mSocialSource);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            checkBackStack();
            return true;
        } else if (i == R.id.ucw_action_sign_out) {
            signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ucw_social_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onError(String message) {
        showError(message);
    }

    @Override
    public void onFileSelected(String fileUrl) {
        selectFile(fileUrl);
    }

    @Override
    public void onAuthorizationNeeded(ChunkResponse chunkResponse) {
        needAuthorization(chunkResponse);
    }

    @Override
    public int currentRootChunk() {
        return mSpinner.getSelectedItemPosition();
    }

    @Override
    public void onChunkSelected(List<Chunk> chunks, String title) {
        openChunk(chunks, title);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateChunk(position, null,null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        checkBackStack();
    }
}
