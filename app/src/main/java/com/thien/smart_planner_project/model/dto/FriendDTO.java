package com.thien.smart_planner_project.model.dto;

import com.thien.smart_planner_project.model.User;

public class FriendDTO {
    private String friendId;
    private User user;

    public FriendDTO() {
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
