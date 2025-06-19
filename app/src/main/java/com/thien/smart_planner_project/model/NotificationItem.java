package com.thien.smart_planner_project.model;

public class NotificationItem {
    private String userId;
    private String title;
    private String body;
    private String type;
    private String timestamp;

    public NotificationItem(String userId, String title, String body, String type, String timestamp) {
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.timestamp = timestamp;
    }

    // Getter methods
    public String getId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getType() { return type; }
    public String getTimestamp() { return timestamp; }
}