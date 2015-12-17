package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

public class Thing {
    public static final String TYPE_ALBUM ="album";/* album, folder, friend, etc. */
    public static final String TYPE_FOLDER ="folder";
    public static final String TYPE_FRIEND ="frined";
    public static final String TYPE_PHOTO ="photo";/* photo, file, etc. */
    public static final String TYPE_FILE ="file";
    @SerializedName("obj_type")
    public String objectType;

    public String mimetype;

    public String title;

    public Action action;

    public String thumbnail;

    @Override
    public String toString() {
        return "Thing{" +
                "objectType='" + objectType + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", title='" + title + '\'' +
                ", action=" + action +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
