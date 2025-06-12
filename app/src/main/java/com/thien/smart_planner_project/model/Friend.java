package com.thien.smart_planner_project.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Friend implements Serializable {
    private String userA;
    private String userB;
    private Map<String, String> status;
    private LocalDateTime createAt;

    public Friend() {
    }

    public Friend(String userA, String userB, Map<String, String> status, LocalDateTime createAt) {
        this.userA = userA;
        this.userB = userB;
        this.status = status;
        this.createAt = createAt;
    }

    public String getUserA() {
        return userA;
    }

    public void setUserA(String userA) {
        this.userA = userA;
    }

    public String getUserB() {
        return userB;
    }

    public void setUserB(String userB) {
        this.userB = userB;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
