package com.thien.smart_planner_project.model;

import java.util.List;
import java.util.Map;

public class Attendee {
    private String attendeeId; // ID duy nhất của người tham gia
    private String userId; // Liên kết với User
    private List<String> eventsRegistered; // Danh sách ID sự kiện đã đăng ký
    private Map<String, String> qrCodes; // Mã QR cho từng sự kiện

    // Constructors, Getters, Setters

    public Attendee(String attendeeId, String userId, List<String> eventsRegistered, Map<String, String> qrCodes) {
        this.attendeeId = attendeeId;
        this.userId = userId;
        this.eventsRegistered = eventsRegistered;
        this.qrCodes = qrCodes;
    }

    public String getAttendeeId() {
        return attendeeId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getEventsRegistered() {
        return eventsRegistered;
    }

    public Map<String, String> getQrCodes() {
        return qrCodes;
    }

    public void setAttendeeId(String attendeeId) {
        this.attendeeId = attendeeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventsRegistered(List<String> eventsRegistered) {
        this.eventsRegistered = eventsRegistered;
    }

    public void setQrCodes(Map<String, String> qrCodes) {
        this.qrCodes = qrCodes;
    }

}