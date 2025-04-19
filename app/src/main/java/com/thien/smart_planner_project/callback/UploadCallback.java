package com.thien.smart_planner_project.callback;

public interface UploadCallback {
    void onUploadSuccess(String imageUrl);
    void onUploadFailure(Throwable t);
}
