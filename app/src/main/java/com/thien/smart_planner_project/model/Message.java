package com.thien.smart_planner_project.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String friendId;
    private String sender;
    private String content;
    private String status;
    private boolean isRead;
    private LocalDateTime createAt;

    public Message() {
    }

    public Message(String content, String status, boolean isRead, String friendId, String sender, LocalDateTime createAt) {
        this.content = content;
        this.status = status;
        this.isRead = isRead;
        this.friendId = friendId;
        this.sender = sender;
        this.createAt = createAt;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
