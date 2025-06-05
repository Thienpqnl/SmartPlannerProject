package com.thien.smart_planner_project.model;

public class CheckinRequest {
    private String userId;
    private String eventId;

    public CheckinRequest() {
    }

    public CheckinRequest(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}


