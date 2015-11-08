package com.uploadcare.android.library.data;

public interface DataWrapper<T, U> {

    T wrap(U data);

}