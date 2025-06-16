package com.thien.smart_planner_project.model.dto;

import java.io.Serializable;

public class StatusResponse implements Serializable {
    private String from;
    private String to;
    private String statusFrom;
    private String statusTo;

    public StatusResponse() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStatusTo() {
        return statusTo;
    }

    public void setStatusTo(String statusTo) {
        this.statusTo = statusTo;
    }

    public String getStatusFrom() {
        return statusFrom;
    }

    public void setStatusFrom(String statusFrom) {
        this.statusFrom = statusFrom;
    }
}
