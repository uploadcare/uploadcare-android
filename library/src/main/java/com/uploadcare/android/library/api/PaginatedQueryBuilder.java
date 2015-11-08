package com.uploadcare.android.library.api;

import java.util.List;

public interface PaginatedQueryBuilder<T> {

    /**
     * Returns a resource iterable for lazy loading.
     */
    Iterable<T> asIterable();

    /**
     * Iterates through all resources and returns a complete list.
     */
    List<T> asList();

}