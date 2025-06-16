package com.thien.smart_planner_project.model.dto;

public class StatusFriend {
    private String from;
    private String to;

    public StatusFriend() {
    }

    public StatusFriend(String from, String to) {
        this.from = from;
        this.to = to;
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
}
