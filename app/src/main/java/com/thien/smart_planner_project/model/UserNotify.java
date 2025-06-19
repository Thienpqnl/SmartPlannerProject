package com.thien.smart_planner_project.model;

public class UserNotify {
    private String userId;
    private String fcmToken;

    public UserNotify(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}