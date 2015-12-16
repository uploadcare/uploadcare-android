package com.uploadcare.android.widget.data;


import com.google.gson.annotations.SerializedName;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.List;

public class SocialSource implements Parcelable {

    @SerializedName("root_chunks")
    public List<Chunk> rootChunks;

    public String name;

    public Urls urls;

    public void saveCookie(Context context, String cookie) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("UCW_PREF_" + name, cookie).commit();
    }

    public String getCookie(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("UCW_PREF_" + name, null);
    }

    public void deleteCookie(Context context) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(null);
        }else {
            CookieManager.getInstance().removeAllCookie();
        }
        preferences.edit().remove("UCW_PREF_" + name).commit();
    }

    public static final Creator<SocialSource> CREATOR = new Creator<SocialSource>() {
        @NonNull
        public SocialSource createFromParcel(@NonNull Parcel in) {
            return new SocialSource(in);
        }

        @NonNull
        public SocialSource[] newArray(int size) {
            return new SocialSource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(urls, flags);
        dest.writeList(rootChunks);
    }

    public SocialSource() {

    }

    /**
     * This will be used only by the Creator
     *
     * @param source source where to read the parceled data
     */
    public SocialSource(@NonNull Parcel source) {
        // reconstruct from the parcel
        this.name = source.readString();
        this.urls = source.readParcelable(Urls.class.getClassLoader());
        this.rootChunks = new ArrayList<>();
        source.readList(rootChunks, Chunk.class.getClassLoader());
    }

    @Override
    public String toString() {
        return "SocialSource{" +
                "rootChunks=" + rootChunks +
                ", name='" + name + '\'' +
                ", urls=" + urls +
                '}';
    }

    public static class Urls implements Parcelable {

        @SerializedName("source_base")
        public String sourceBase;

        public String session;

        public String done;

        public static final Creator<Urls> CREATOR = new Creator<Urls>() {
            @NonNull
            public Urls createFromParcel(@NonNull Parcel in) {
                return new Urls(in);
            }

            @NonNull
            public Urls[] newArray(int size) {
                return new Urls[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sourceBase);
            dest.writeString(session);
            dest.writeString(done);
        }

        public Urls() {

        }

        /**
         * This will be used only by the Creator
         *
         * @param source source where to read the parceled data
         */
        public Urls(@NonNull Parcel source) {
            // reconstruct from the parcel
            this.sourceBase = source.readString();
            this.session = source.readString();
            this.done = source.readString();
        }

        @Override
        public String toString() {
            return "Urls{" +
                    "sourceBase='" + sourceBase + '\'' +
                    ", session='" + session + '\'' +
                    ", done='" + done + '\'' +
                    '}';
        }
    }
}

