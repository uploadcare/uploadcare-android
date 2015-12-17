package com.uploadcare.android.widget.data;

import com.uploadcare.android.widget.R;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SocialSourcesResponse implements Parcelable {

    public List<SocialSource> sources;

    public static final Parcelable.Creator<SocialSourcesResponse>
            CREATOR = new Parcelable.Creator<SocialSourcesResponse>() {
        @NonNull
        public SocialSourcesResponse createFromParcel(@NonNull Parcel in) {
            return new SocialSourcesResponse(in);
        }

        @NonNull
        public SocialSourcesResponse[] newArray(int size) {
            return new SocialSourcesResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(sources);
    }

    public SocialSourcesResponse() {

    }

    /**
     * This will be used only by the Creator
     *
     * @param source source where to read the parceled data
     */
    public SocialSourcesResponse(@NonNull Parcel source) {
        // reconstruct from the parcel
        this.sources = new ArrayList<>();
        source.readList(sources, SocialSource.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "SocialSourcesResponse{" +
                "sources=" + sources +
                '}';
    }

    public static int getNetworkNameResource(SocialSource source){
        switch (source.name){
            case "facebook":
                return R.string.ucw_social_facebook;
            case "instagram":
                return R.string.ucw_social_instagram;
            case "vk":
                return R.string.ucw_social_vk;
            case "box":
                return R.string.ucw_social_box;
            case "huddle":
                return R.string.ucw_social_huddle;
            case "flickr":
                return R.string.ucw_social_flickr;
            case "evernote":
                return R.string.ucw_social_evernote;
            case "skydrive":
                return R.string.ucw_social_skydrive;
            case "dropbox":
                return R.string.ucw_social_dropbox;
            case "gdrive":
                return R.string.ucw_social_gdrive;
            case "video":
                return R.string.ucw_social_video;
            case "image":
                return R.string.ucw_social_image;
            case "file":
                return R.string.ucw_social_file;
            default:
                return R.string.ucw_social_unknown;
        }
    }

    public static int getNetworkIconResource(SocialSource source){
        switch (source.name){
            case "facebook":
                return R.drawable.ucw_facebook_icon;
            case "instagram":
                return R.drawable.ucw_instagram_icon;
            case "vk":
                return R.drawable.ucw_vkontakte_icon;
            case "box":
                return R.drawable.ucw_box_icon;
            case "huddle":
                return R.drawable.ucw_huddle_icon;
            case "flickr":
                return R.drawable.ucw_flickr_icon;
            case "evernote":
                return R.drawable.ucw_evenote_icon;
            case "skydrive":
                return R.drawable.ucw_onedrive_icon;
            case "dropbox":
                return R.drawable.ucw_dropbox_icon;
            case "gdrive":
                return R.drawable.ucw_googledrive_icon;
            case "video":
                return R.drawable.ic_videocam_white_24dp;
            case "image":
                return R.drawable.ic_photo_camera_white_24dp;
            case "file":
                return R.drawable.ic_insert_photo_white_24dp;
            default:
                return -1;
        }
    }
}
