package com.thien.smart_planner_project.model;

public class CheckinRequest {
    private String qrCode;
    private String eventId;

    public CheckinRequest(String qrCode, String eventId) {
        this.qrCode = qrCode;
        this.eventId = eventId;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getEventId() {
        return eventId;
    }
}


