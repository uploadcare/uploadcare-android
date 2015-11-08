package com.uploadcare.android.example.adapter;

import com.squareup.picasso.Picasso;
import com.uploadcare.android.example.R;
import com.uploadcare.android.example.util.SquaredImageView;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.urls.CdnPathBuilder;
import com.uploadcare.android.library.urls.Urls;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter
 */
public class UploadcareFileAdapter
        extends RecyclerView.Adapter<UploadcareFileAdapter.UploadcareFileViewHolder> {

    private List<UploadcareFile> mDataset = Collections.emptyList();

    private final Context mContext;

    public interface ItemTapListener {

        public void itemTap(UploadcareFile uploadcareFile);
    }

    private ItemTapListener mItemTapListener;

    public UploadcareFileAdapter(Context context, ItemTapListener listener) {
        mContext = context;
        mItemTapListener = listener;
        mDataset = new ArrayList<UploadcareFile>();
    }

    public static class UploadcareFileViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SquaredImageView image;

        public FrameLayout itemRoot;

        public UploadcareFileViewHolder(View v) {
            super(v);
            itemRoot = (FrameLayout) v.findViewById(R.id.item_root);
            title = (TextView) v.findViewById(R.id.title);
            image = (SquaredImageView) v.findViewById(R.id.image);
        }
    }

    @Override
    public UploadcareFileAdapter.UploadcareFileViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.file_item, parent, false);
        view.setClickable(true);
        view.setFocusable(true);
        return new UploadcareFileViewHolder(view);
    }

    /**
     * Populate view with data.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final UploadcareFileAdapter.UploadcareFileViewHolder holder,
            final int position) {
        final UploadcareFile file = mDataset.get(position);
        holder.title.setText(file.toString());
        CdnPathBuilder builder = file.cdnPath();
        builder.resizeWidth(250);
        builder.cropCenter(250, 250);
        URI url = Urls.cdn(builder);
        Picasso.with(mContext).load(url.toString()).into(
                holder.image);
        holder.itemRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemTapListener.itemTap(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Update existing Dataset.
     */
    public void updateFiles(List<UploadcareFile> nDataset) {
        mDataset = nDataset;
        notifyDataSetChanged();
    }

    /**
     * Add List of UploadcareFile objects to the end of existing Dataset.
     */
    public void addFiles(List<UploadcareFile> nDataset) {
        mDataset.addAll(nDataset);
        notifyDataSetChanged();
    }

    /**
     * Clear Dataset.
     */
    public void clear() {
        mDataset.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mDataset.isEmpty();
    }
}
