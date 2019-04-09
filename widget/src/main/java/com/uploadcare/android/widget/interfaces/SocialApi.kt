package com.uploadcare.android.widget.interfaces

import com.uploadcare.android.widget.data.ChunkResponse
import com.uploadcare.android.widget.data.SelectedFile
import com.uploadcare.android.widget.data.SocialSourcesResponse
import retrofit2.Call
import retrofit2.http.*

interface SocialApi {

    @GET("sources")
    fun getSources(): Call<SocialSourcesResponse>

    @GET("{base}/{chunk}/{offset}")
    fun getSourceChunk(@Header("Cookie") authCookie: String,
                       @Path("base") sourceBase: String,
                       @Path("chunk") chunk: String,
                       @Path("offset") loadMore: String): Call<ChunkResponse>

    @DELETE("{session}")
    fun signOut(@Header("Cookie") authCookie: String, @Path("session") sourceBase: String)
            : Call<Any>

    @FormUrlEncoded
    @POST("{done}")
    fun selectFile(@Header("Cookie") authCookie: String,
                   @Path("done") done: String,
                   @Field("file") fileUrl: String): Call<SelectedFile>
}