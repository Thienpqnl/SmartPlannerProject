package com.thien.smart_planner_project.model;

import java.util.List;

public class SendMailInviteDTO {
    private String from;
    private String to;
    private String subject;
    private List<Event> events;

    public SendMailInviteDTO(String from, String to, String subject, List<Event> events) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.events = events;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
