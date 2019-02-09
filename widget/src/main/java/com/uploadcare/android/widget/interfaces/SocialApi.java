package com.uploadcare.android.widget.interfaces;

import com.uploadcare.android.widget.data.ChunkResponse;
import com.uploadcare.android.widget.data.SelectedFile;
import com.uploadcare.android.widget.data.SocialSourcesResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface SocialApi {

    @GET("sources")
    Call<SocialSourcesResponse> getSources();

    @GET("{base}/{chunk}/{offset}")
    Call<ChunkResponse> getSourceChunk(@Header("Cookie") String authCookie,
                                       @Path("base") String sourceBase,
                                       @Path("chunk") String chunk,
                                       @Path("offset") String loadMore);

    @DELETE("{session}")
    Call<Response> signOut(@Header("Cookie") String authCookie, @Path("session") String sourceBase);

    @FormUrlEncoded
    @POST("{done}")
    Call<SelectedFile> selectFile(@Header("Cookie") String authCookie,
                                  @Path("done") String done,
                                  @Field("file") String fileUrl);
}
