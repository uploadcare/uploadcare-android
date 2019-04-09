package com.uploadcare.android.widget.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Class with pagination features for RecyclerView.OnScrollListener
 */
class RecyclerViewOnScrollListener(layoutManager: RecyclerView.LayoutManager,
                                   private val loadMoreObserver: (() -> Unit)? = null)
    : RecyclerView.OnScrollListener() {

    private var previousTotal = 0 // The total number of items in the dataset after the last load

    private var loading = true // True if we are still waiting for the last set of data to load.

    private var visibleThreshold = 20 // The minimum amount of items to have below your current
    // scroll position before loading more.

    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    private var gridLayoutManager: GridLayoutManager? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    init {
        if (layoutManager is GridLayoutManager) {
            gridLayoutManager = layoutManager
        } else {
            linearLayoutManager = layoutManager as LinearLayoutManager
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        gridLayoutManager?.let { layoutManager ->
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        }

        linearLayoutManager?.let { layoutManager ->
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        }

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            loadMoreObserver?.let { it() }

            loading = true
        }
    }

    fun clear() {
        previousTotal = 0
        loading = false
    }
}
