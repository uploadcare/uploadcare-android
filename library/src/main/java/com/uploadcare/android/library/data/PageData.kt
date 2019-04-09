package com.uploadcare.android.library.data

import java.net.URI

interface PageData<T> {
    fun getResultsData(): List<T>

    fun hasMore(): Boolean

    fun getNextURI(): URI?
}