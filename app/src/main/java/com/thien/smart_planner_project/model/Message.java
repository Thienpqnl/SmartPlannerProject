package com.thien.smart_planner_project.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private String friendId;
    private String sender;
    private String content;
    private String status;
    private boolean isRead;
    @SerializedName("createdAt")
    private String createdAt;

    public Message() {
    }

    public Message(String content, String status, boolean isRead, String friendId, String sender, String createdAt) {
        this.content = content;
        this.status = status;
        this.isRead = isRead;
        this.friendId = friendId;
        this.sender = sender;
        this.createdAt = createdAt;
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

    public String getcreatedAt() {
        return createdAt;
    }

    public void setcreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAtDateTime() {
        if (createdAt == null) return null;
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public String toString() {
        return "Message{" +
                "friendId='" + friendId + '\'' +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
