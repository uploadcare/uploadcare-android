package com.uploadcare.android.widget.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Class with pagination features for RecyclerView.OnScrollListener
 */
public abstract class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    private int previousTotal = 0; // The total number of items in the dataset after the last load

    private boolean loading = true;
    // True if we are still waiting for the last set of data to load.

    private final int visibleThreshold = 20;//test
    // The minimum amount of items to have below your current scroll position before loading more.

    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private GridLayoutManager mGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerViewOnScrollListener(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            mGridLayoutManager= (GridLayoutManager) layoutManager;
        }else {
            mLinearLayoutManager=(LinearLayoutManager)layoutManager;
        }
    }

    //called when scrolled to the bottom
    public abstract void onLoadMore();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        if(mGridLayoutManager!=null) {
            totalItemCount = mGridLayoutManager.getItemCount();
            firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
        }else {
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        }
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            onLoadMore();

            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public void clear(){
        previousTotal=0;
        loading=false;
    }
}
