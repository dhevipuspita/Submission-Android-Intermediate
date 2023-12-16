package com.dicoding.MyStoryApp.data.network

import com.dicoding.MyStoryApp.data.response.AddNewStoryResponse
import com.dicoding.MyStoryApp.data.response.DetailStoryResponse
import com.dicoding.MyStoryApp.data.response.LoginResponse
import com.dicoding.MyStoryApp.data.response.RegisterResponse
import com.dicoding.MyStoryApp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun upStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddNewStoryResponse

    @GET("stories")
    suspend fun getStoriesLocation(
        @Query("location") location : Int = 1,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 100
    ): StoryResponse
}