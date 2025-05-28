package com.thien.smart_planner_project.network;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {
    @SerializedName("imageUrl")
    private String imageUrl;
    public String getImageUrl() {
        return imageUrl;
    }
}