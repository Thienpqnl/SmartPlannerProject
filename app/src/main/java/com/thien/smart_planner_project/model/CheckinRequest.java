package com.thien.smart_planner_project.model;

public class CheckinRequest {
    private String qrCode;

    public CheckinRequest(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}

