package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChunkResponse {

    @SerializedName("login_link")
    public String loginLink;

    @SerializedName("obj_type")
    public String objectType;//"thing_set"//error

    public String error;

    @SerializedName("next_page")
    public Path nextPage;

    public Chunk root;

    public List<Thing> things;

    @SerializedName("search_path")
    public Chunk searchPath;

    @Override
    public String toString() {
        return "ChunkResponse{" +
                "loginLink='" + loginLink + '\'' +
                ", objectType='" + objectType + '\'' +
                ", error='" + error + '\'' +
                ", nextPage=" + nextPage +
                ", root=" + root +
                ", things=" + things +
                ", searchPath=" + searchPath +
                '}';
    }
}
