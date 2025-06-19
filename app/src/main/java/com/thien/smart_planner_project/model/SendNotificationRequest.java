package com.thien.smart_planner_project.model;

public class SendNotificationRequest {
    private String userId;
    private String title;
    private String body;
    private String type;

    public SendNotificationRequest(String userId, String title, String body, String type) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
    }
}