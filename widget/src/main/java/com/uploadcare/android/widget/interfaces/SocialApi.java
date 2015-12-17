package com.uploadcare.android.widget.interfaces;

import com.uploadcare.android.widget.data.ChunkResponse;
import com.uploadcare.android.widget.data.SelectedFile;
import com.uploadcare.android.widget.data.SocialSourcesResponse;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SocialApi {

    @GET("/sources")
    void getSources(Callback<SocialSourcesResponse> cb);

    @GET("/{base}/{chunk}/{offset}")
    void getSourceChunk(@Header("Cookie") String authCookie, @Path("base") String sourceBase,@Path("chunk") String chunk,@Path("offset") String loadMore,Callback<ChunkResponse> cb);

    @DELETE("/{session}")
    void signOut(@Header("Cookie") String authCookie, @Path("session") String sourceBase,Callback<Response> cb);

    @FormUrlEncoded
    @POST("/{done}")
    void selectFile(@Header("Cookie") String authCookie,@Path("done") String done,@Field("file") String fileUrl, Callback<SelectedFile> cb);
}
