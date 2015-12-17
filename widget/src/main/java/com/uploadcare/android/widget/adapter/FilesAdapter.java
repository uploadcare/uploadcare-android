package com.uploadcare.android.widget.adapter;


import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.controller.UploadcareWidget;
import com.uploadcare.android.widget.data.Action;
import com.uploadcare.android.widget.data.Path;
import com.uploadcare.android.widget.data.Thing;
import com.uploadcare.android.widget.interfaces.ItemTapListener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilesAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    List<Thing> mDataset = Collections.emptyList();

    final Context mContext;

    private Path nextPath;

    final ItemTapListener mItemTapListener;

    @UploadcareWidget.FileType
    private final String mFileType;

    FilesAdapter(Context context, ItemTapListener listener, @UploadcareWidget.FileType String fileType) {
        mContext = context;
        mItemTapListener = listener;
        mDataset = new ArrayList<>();
        mFileType=fileType;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getNext() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nextPath.chunks.size(); i++) {
            if (i != nextPath.chunks.size() - 1) {
                stringBuilder.append(nextPath.chunks.get(i).pathChunk).append("/");
            } else {
                stringBuilder.append(nextPath.chunks.get(i).pathChunk);
            }
        }
        return stringBuilder.toString();
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

    int getPlaceHolderResource(Thing thing) {
        int placeHolderResource = R.drawable.ucw_ic_file_grey_48dp;
        if (thing.objectType.equalsIgnoreCase(Thing.TYPE_ALBUM) || thing.objectType
                .equalsIgnoreCase(Thing.TYPE_FOLDER)) {
            placeHolderResource = R.drawable.ucw_ic_folder_grey_48dp;
        } else if (thing.objectType.equalsIgnoreCase(Thing.TYPE_PHOTO) && (
                (thing.thumbnail != null && thing.thumbnail.contains("http")) || (
                        thing.action.action.equalsIgnoreCase(Action.ACTION_SELECT_FILE)
                                && thing.action.url.matches(".*((\\.jpg)|(\\.png)).*")))) {
            placeHolderResource = R.drawable.ucw_ic_photo_grey_48dp;
        }
        return placeHolderResource;
    }

    int getLinearPlaceHolderResource(Thing thing) {
        int placeHolderResource = R.drawable.ucw_ic_file_grey_24dp;
        if (thing.objectType.equalsIgnoreCase(Thing.TYPE_ALBUM) || thing.objectType
                .equalsIgnoreCase(Thing.TYPE_FOLDER)) {
            placeHolderResource = R.drawable.ucw_ic_folder_grey_24dp;
        } else if (thing.objectType.equalsIgnoreCase(Thing.TYPE_PHOTO) && (
                (thing.thumbnail != null && thing.thumbnail.contains("http")) || (
                        thing.action.action.equalsIgnoreCase(Action.ACTION_SELECT_FILE)
                                && thing.action.url.matches(".*((\\.jpg)|(\\.png)).*")))) {
            placeHolderResource = R.drawable.ucw_ic_photo_grey_24dp;
        }
        return placeHolderResource;
    }

    /**
     * Add List of Thing objects to the end of existing Dataset.
     */
    public void addImages(List<Thing> nDataset, Path nextPath) {
        this.nextPath = nextPath;
        if(mFileType.equalsIgnoreCase(UploadcareWidget.FILE_TYPE_ANY)){
            mDataset.addAll(nDataset);
            notifyDataSetChanged();
        }else {
            for(Thing thing : nDataset){
                if (thing.objectType.equalsIgnoreCase(Thing.TYPE_ALBUM) || thing.objectType
                        .equalsIgnoreCase(Thing.TYPE_FOLDER)) {
                    mDataset.add(thing);
                    notifyItemInserted(mDataset.size() - 1);
                }else if(thing.mimetype.startsWith(mFileType)){
                    mDataset.add(thing);
                    notifyItemInserted(mDataset.size()-1);
                }
            }
        }

    }

    /**
     * Update existing Dataset.
     */
    public void updateImages(List<Thing> nDataset, Path nextPath) {
        this.nextPath = nextPath;
        if(mFileType.equalsIgnoreCase(UploadcareWidget.FILE_TYPE_ANY)){
            mDataset = nDataset;
            notifyDataSetChanged();
        }else {
            for(Thing thing : nDataset){
                if (thing.objectType.equalsIgnoreCase(Thing.TYPE_ALBUM) || thing.objectType
                        .equalsIgnoreCase(Thing.TYPE_FOLDER)) {
                    mDataset.add(thing);
                    notifyItemInserted(mDataset.size()-1);
                }else if(thing.mimetype!=null&&thing.mimetype.startsWith(mFileType)){
                    mDataset.add(thing);
                    notifyItemInserted(mDataset.size()-1);
                }
            }
        }

    }
}
