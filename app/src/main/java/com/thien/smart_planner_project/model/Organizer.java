package com.thien.smart_planner_project.model;

import java.util.List;

public class Organizer {
    private String organizerId; // ID duy nhất của người tổ chức
    private String userId; // Liên kết với User
    private List<String> eventsCreated; // Danh sách ID sự kiện đã tạo

    // Constructors, Getters, Setters


    public Organizer(String organizerId, String userId, List<String> eventsCreated) {
        this.organizerId = organizerId;
        this.userId = userId;
        this.eventsCreated = eventsCreated;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getEventsCreated() {
        return eventsCreated;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventsCreated(List<String> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }
}
