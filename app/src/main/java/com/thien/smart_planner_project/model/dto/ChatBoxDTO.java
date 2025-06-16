package com.thien.smart_planner_project.model.dto;

import com.thien.smart_planner_project.model.Message;
import com.thien.smart_planner_project.model.User;

public class ChatBoxDTO {
    private String friendId;
    private User user;
    private Message lastMessage;

    public ChatBoxDTO() {
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

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
