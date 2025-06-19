package com.thien.smart_planner_project.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Friend implements Serializable {
    private String userA;
    private String userB;
    private String statusA;
    private String statusB;
    private LocalDateTime createAt;

    public Friend() {
    }

    public Friend(String userA, String userB, String statusA, String statusB, LocalDateTime createAt) {
        this.userA = userA;
        this.userB = userB;
        this.statusA = statusB;
        this.statusA = statusB;
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

    public String getStatusA() {
        return statusA;
    }

    public void setStatusA(String statusA) {
        this.statusA = statusA;
    }

    public String getStatusB() {
        return statusB;
    }

    public void setStatusB(String statusB) {
        this.statusB = statusB;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
