package com.uploadcare.android.library.api

interface PaginatedQueryBuilder<T> {

    /**
     * Returns a resource iterable for lazy loading.
     */
    fun asIterable(): Iterable<T>

    /**
     * Iterates through all resources and returns a complete list.
     */
    fun asList(): List<T>
}