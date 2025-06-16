package com.thien.smart_planner_project.model.dto;

public class MarkReadRequest {
    private String friendId;
    private String userId;

    public MarkReadRequest() {
    }

    public MarkReadRequest(String friendId, String userId) {
        this.friendId = friendId;
        this.userId = userId;
    }

    public String getFriendId() { return friendId; }
    public void setFriendId(String friendId) { this.friendId = friendId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
