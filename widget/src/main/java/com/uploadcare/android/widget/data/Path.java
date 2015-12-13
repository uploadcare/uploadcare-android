package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Path {

    public List<Chunk> chunks;

    @SerializedName("obj_type")
    public String objectType;

    @Override
    public String toString() {
        return "Path{" +
                "chunks=" + chunks +
                ", objectType='" + objectType + '\'' +
                '}';
    }
}
