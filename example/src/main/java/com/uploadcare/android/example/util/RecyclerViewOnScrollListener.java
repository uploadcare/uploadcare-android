package com.uploadcare.android.example.util;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Class with pagination features for RecyclerView.OnScrollListener
 */
public abstract class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load

    private boolean loading = true;
    // True if we are still waiting for the last set of data to load.

    private int visibleThreshold = 4;
    // The minimum amount of items to have below your current scroll position before loading more.

    int firstVisibleItem, visibleItemCount, totalItemCount;

    public int current_page = 0;

    private LinearLayoutManager mLayoutManager;

    public RecyclerViewOnScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    //called when scrolled to the bottom
    public abstract void onLoadMore(int current_page);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public void clear() {
        previousTotal = 0;
        current_page = 0;
    }
}
