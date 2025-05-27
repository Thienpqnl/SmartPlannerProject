package com.thien.smart_planner_project.model;

import java.time.Instant;

public class Booking {
    private String id;
    private String idEvent;
    private String userId;
    private String urlQR;
    private long createdAt;

    public Booking(String idEvent, String userId,long createdAt) {
        this.idEvent = idEvent;
        this.userId = userId;
        this.createdAt=createdAt;
    }

    public String getUrlQR() {
        return urlQR;
    }

    public void setUrlQR(String urlQR) {
        this.urlQR = urlQR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdUser() {
        return userId;
    }

    public void setIdUser(String idUser) {
        this.userId = idUser;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
