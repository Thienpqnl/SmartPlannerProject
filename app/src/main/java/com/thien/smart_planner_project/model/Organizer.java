package com.thien.smart_planner_project.model;

import java.util.ArrayList;
import java.util.List;

public class Organizer {
    private String id;
    private String userId; // Liên kết với User
    private String idEvent; // Danh sách ID sự kiện đã tạo
    private List<String>listIdBooking;
    // Constructors, Getters, Setters
    public Organizer(String id, String userId, String idEvent, List<String> listIdBooking) {
        this.userId = userId;
        this.id=id;
        this.idEvent = idEvent;
        this.listIdBooking = listIdBooking;
    }

    public List<String> getListIdBooking() {
        return listIdBooking;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setListIdBooking(List<String> listIdBooking) {
        this.listIdBooking = listIdBooking;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }
}
