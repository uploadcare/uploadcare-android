package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class Chunk implements Parcelable {

    @SerializedName("obj_type")
    public String objectType;

    @SerializedName("path_chunk")
    public String pathChunk;

    public String title;

    public static final Creator<Chunk> CREATOR = new Creator<Chunk>() {
        @NonNull
        public Chunk createFromParcel(@NonNull Parcel in) {
            return new Chunk(in);
        }

        @NonNull
        public Chunk[] newArray(int size) {
            return new Chunk[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectType);
        dest.writeString(pathChunk);
        dest.writeString(title);
    }

    public Chunk() {

    }

    /**
     * This will be used only by the Creator
     *
     * @param source source where to read the parceled data
     */
    public Chunk(@NonNull Parcel source) {
        // reconstruct from the parcel
        this.objectType=source.readString();
        this.pathChunk=source.readString();
        this.title=source.readString();
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "objectType='" + objectType + '\'' +
                ", pathChunk='" + pathChunk + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
