package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Action {
    public static final String ACTION_OPEN_PATH ="open_path";
    public static final String ACTION_SELECT_FILE ="select_file";
    @SerializedName("obj_type")
    public String objectType;

    public String action;

    public String url;

    public Path path;

    @Override
    public String toString() {
        return "Action{" +
                "objectType='" + objectType + '\'' +
                ", action='" + action + '\'' +
                ", url='" + url + '\'' +
                ", path=" + path +
                '}';
    }

    public static class Path{
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

}
