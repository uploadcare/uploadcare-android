package com.uploadcare.android.widget.adapter;

import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.data.SocialSource;
import com.uploadcare.android.widget.data.SocialSourcesResponse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SocialNetworksAdapter extends ArrayAdapter<SocialSource> {

    private List<SocialSource> mItems = new ArrayList<>();

    private final LayoutInflater inflater;

    public SocialNetworksAdapter(Context context, List<SocialSource> objects) {
        super(context, R.layout.ucw_dialog_network_item);
        mItems = objects;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void clear() {
        super.clear();
        mItems.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ucw_dialog_network_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.ucw_network_name);
        textView.setText(getTitle(position));
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ucw_network_icon);
        int icon = getIcon(position);
        if (icon != -1) {
            imageView.setImageResource(icon);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public SocialSource getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getTitle(int position) {
        return position >= 0 && position < mItems.size() ? SocialSourcesResponse
                .getNetworkNameResource(mItems.get(position)) : R.string.ucw_social_unknown;
    }

    private int getIcon(int position) {
        return position >= 0 && position < mItems.size() ? SocialSourcesResponse
                .getNetworkIconResource(mItems.get(position)) : -1;
    }
}
