package com.uploadcare.android.widget.adapter;

import com.uploadcare.android.widget.R;
import com.uploadcare.android.widget.data.Chunk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ToolbarSpinnerAdapter extends ArrayAdapter<Chunk> {

    private List<Chunk> mItems = new ArrayList<>();

    private final LayoutInflater inflater;

    public ToolbarSpinnerAdapter(Context context, List<Chunk> objects) {
        super(context, R.layout.ucw_spinner_row, objects);
        mItems = objects;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void clear() {
        super.clear();
        mItems.clear();
    }

    public void addItem(Chunk chunk) {
        mItems.add(chunk);
        notifyDataSetChanged();
    }

    public void addItems(List<Chunk> chunks) {
        mItems.addAll(chunks);
        notifyDataSetChanged();
    }

    public void updateItems(List<Chunk> chunks){
        mItems=chunks;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Chunk getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = inflater.inflate(R.layout.ucw_spinner_row, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(R.id.ucw_spinner_title);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = inflater.inflate(R.layout.
                    ucw_toolbar_spinner_item, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(R.id.ucw_spinner_title);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
    }
}
