package com.thien.smart_planner_project.network;

import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.ImageUploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadAPI {
    @Multipart
    @POST("/upload")
    Call<ImageUploadResponse> uploadImage(@Part MultipartBody.Part image);
    @GET("/upload")
    Call<List<ImageUploadResponse>> getAllEvents();
}