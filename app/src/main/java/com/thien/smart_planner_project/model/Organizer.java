package com.thien.smart_planner_project.model;

import java.util.ArrayList;
import java.util.List;

public class Organizer {
    private String userId; // Liên kết với User
    private List<String> eventsCreated; // Danh sách ID sự kiện đã tạo
    private List<String>listIdBooking;
    // Constructors, Getters, Setters
    public Organizer( String userId, List<String> eventsCreated, List<String> listIdBooking) {
        this.userId = userId;
        this.eventsCreated = eventsCreated;
        this.listIdBooking = listIdBooking;
    }

    public List<String> getListIdBooking() {
        return listIdBooking;
    }

    public void setListIdBooking(List<String> listIdBooking) {
        this.listIdBooking = listIdBooking;
    }
    public String getUserId() {
        return userId;
    }
    public List<String> getEventsCreated() {
        return eventsCreated;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventsCreated(List<String> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }
}
