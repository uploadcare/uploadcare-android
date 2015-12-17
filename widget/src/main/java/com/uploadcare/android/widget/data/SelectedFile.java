package com.uploadcare.android.widget.data;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class SelectedFile implements Parcelable {

    @SerializedName("obj_type")
    public String objectType;

    public String url;

    @SerializedName("is_image")
    public boolean isImage;

    public static final Creator<SelectedFile> CREATOR = new Creator<SelectedFile>() {
        @NonNull
        public SelectedFile createFromParcel(@NonNull Parcel in) {
            return new SelectedFile(in);
        }

        @NonNull
        public SelectedFile[] newArray(int size) {
            return new SelectedFile[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectType);
        dest.writeString(url);
        dest.writeInt(isImage?1:0);
    }

    public SelectedFile() {

    }

    /**
     * This will be used only by the Creator
     *
     * @param source source where to read the parceled data
     */
    public SelectedFile(@NonNull Parcel source) {
        // reconstruct from the parcel
        this.objectType=source.readString();
        this.url=source.readString();
        this.isImage=(source.readInt()==1);
    }

    @Override
    public String toString() {
        return "SelectedFile{" +
                "objectType='" + objectType + '\'' +
                ", url='" + url + '\'' +
                ", isImage=" + isImage +
                '}';
    }
}
