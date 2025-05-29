package com.thien.smart_planner_project.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Booking {
    private String id;
    private String idEvent;
    private String creatorId;
    private String userId;
    private String urlQR;
    private String date; // ISO-8601 format (e.g., "2025-05-29")
    private String time; // ISO-8601 format (e.g., "12:34:56")

    // Constructor
    public Booking(String idEvent, String creatorId, String userId) {
        this.idEvent = idEvent;
        this.creatorId = creatorId;
        this.userId = userId;
        this.date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        this.time = LocalTime.now().format(DateTimeFormatter.ISO_TIME);
    }

    // toString override
    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", idEvent='" + idEvent + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", userId='" + userId + '\'' +
                ", urlQR='" + urlQR + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    // Getters and setters
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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlQR() {
        return urlQR;
    }

    public void setUrlQR(String urlQR) {
        this.urlQR = urlQR;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
