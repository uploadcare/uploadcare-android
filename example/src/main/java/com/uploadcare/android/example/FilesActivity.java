package com.uploadcare.android.example;

import com.uploadcare.android.example.adapter.UploadcareFileAdapter;
import com.uploadcare.android.example.util.RecyclerViewOnScrollListener;
import com.uploadcare.android.library.api.FilesQueryBuilder;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFilesCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity with RecycleView dynamically populated with a list of Uploadcarefile files.
 */
public class FilesActivity extends AppCompatActivity
        implements UploadcareFileAdapter.ItemTapListener,
        View.OnClickListener, CheckBox.OnCheckedChangeListener {

    private final int ITEMS_PER_PAGE = 30;

    private final int FROM_DATE_PICK = 555;

    private final int TO_DATE_PICK = 666;

    UploadcareClient client;

    RecyclerView mRecyclerView;

    private UploadcareFileAdapter mUploadcareFileAdapter;

    private LinearLayoutManager mLayoutManager;

    private RecyclerViewOnScrollListener mRecyclerViewOnScrollListener;

    private URI next = null;

    private View mRootView;

    private TextView mStatusTextView;

    private CheckBox storedCheckBox, removedCheckBox;

    private TextView dateFilterTextView;

    private boolean filterStored = false;

    private boolean filterRemoved = false;

    private Date filterFromDate = null;

    private Date filterToDate = null;

    /**
     * Initialize variables and creates {@link UploadcareClient}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_files);
        client = UploadcareClient.demoClient();
        findViewById(R.id.btn_from).setOnClickListener(this);
        findViewById(R.id.btn_to).setOnClickListener(this);
        findViewById(R.id.btn_apply).setOnClickListener(this);
        mRootView = findViewById(R.id.root);
        mStatusTextView = (TextView) findViewById(R.id.status_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        storedCheckBox = (CheckBox) findViewById(R.id.stored);
        removedCheckBox = (CheckBox) findViewById(R.id.removed);
        dateFilterTextView = (TextView) findViewById(R.id.date_filter);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mUploadcareFileAdapter = new UploadcareFileAdapter(this, this);
        mRecyclerView.setAdapter(mUploadcareFileAdapter);
        mRecyclerViewOnScrollListener = new RecyclerViewOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (next != null) {
                    getFiles(next);
                }
            }
        };
        mRecyclerView.addOnScrollListener(mRecyclerViewOnScrollListener);

        storedCheckBox.setOnCheckedChangeListener(this);
        removedCheckBox.setOnCheckedChangeListener(this);
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

    /**
     * Launches {@link CdnActivity} when user clicks on item.
     *
     * @param uploadcareFile {@link UploadcareFile} which user clicked.
     */
    @Override
    public void itemTap(UploadcareFile uploadcareFile) {
        Intent intent = new Intent(this, CdnActivity.class);
        intent.putExtra("fileId", uploadcareFile.getFileId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_from:
                showDateDialog(FROM_DATE_PICK);
                break;
            case R.id.btn_to:
                showDateDialog(TO_DATE_PICK);
                break;
            case R.id.btn_apply:
                getFiles(null);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.stored:
                filterStored = isChecked;
                break;
            case R.id.removed:
                filterRemoved = isChecked;
                break;
        }
    }

    /**
     * Get Uploadcare files data with {@link UploadcareClient}.
     *
     * @param nextItems page offset.
     */
    private void getFiles(final URI nextItems) {
        if (nextItems == null) {
            mUploadcareFileAdapter.clear();
            mRecyclerViewOnScrollListener.clear();
            showStatus(getString(R.string.activity_files_status_loading), false);
        }
        FilesQueryBuilder filesQueryBuilder = client.getFiles();
        if (filterStored) {
            filesQueryBuilder.stored(true);
        } else if (filterRemoved) {
            filesQueryBuilder.removed(true);
        }

        if (filterFromDate != null) {
            filesQueryBuilder.from(filterFromDate);
        } else if (filterToDate != null) {
            filesQueryBuilder.to(filterToDate);
        }

        filesQueryBuilder.asListAsync(this, ITEMS_PER_PAGE, nextItems,
                new UploadcareFilesCallback() {
                    @Override
                    public void onFailure(UploadcareApiException e) {
                        hideStatus();
                        showErrorMessage(e.getLocalizedMessage());
                    }

                    @Override
                    public void onSuccess(List<UploadcareFile> files, URI next) {
                        hideStatus();
                        if (nextItems != null) {
                            mUploadcareFileAdapter.addFiles(files);
                        } else {
                            mUploadcareFileAdapter.updateFiles(files);
                            if (mUploadcareFileAdapter.isEmpty()) {
                                showStatus(getString(R.string.activity_files_no_items), true);
                            }
                        }
                        FilesActivity.this.next = next;
                    }
                });
    }

    /**
     * Shows status info when Adapter is empty
     *
     * @param message to show.
     * @param error   if set shows Retry button.
     */
    private void showStatus(String message, boolean error) {
        if (!error && !mUploadcareFileAdapter.isEmpty()) {
            hideStatus();
            return;
        }
        mStatusTextView.setText(message);
        mStatusTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides status View
     */
    private void hideStatus() {
        mStatusTextView.setVisibility(View.GONE);
    }

    /**
     * Shows Snackbar with message if Adapter has data,
     * activates Status views if Adapter has no data.
     *
     * @param message to show.
     */
    private void showErrorMessage(String message) {
        if (!mUploadcareFileAdapter.isEmpty()) {
            hideStatus();
            Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG).show();
        } else {
            showStatus(message, true);
        }
    }

    /**
     * Launches date picker dialog for filters.
     *
     * @param type type of filter to choose date for.
     */
    private void showDateDialog(final int type) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                            int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        if (type == FROM_DATE_PICK) {
                            try {
                                filterFromDate = dateFormat
                                        .parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dateFilterTextView.setText(
                                    getResources().getString(R.string.activity_files_btn_from) + ":"
                                            + filterFromDate.toString());
                            filterToDate = null;
                        } else if (type == TO_DATE_PICK) {
                            try {
                                filterToDate = dateFormat
                                        .parse(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dateFilterTextView.setText(
                                    getResources().getString(R.string.activity_files_btn_to) + ":"
                                            + filterToDate.toString());
                            filterFromDate = null;
                        }
                    }
                }, mYear, mMonth, mDay);
        dialog.show();
    }
}
