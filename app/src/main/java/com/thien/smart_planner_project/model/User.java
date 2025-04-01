package com.thien.smart_planner_project.model;

public class User {
    private String userId; // ID duy nhất của người dùng
    private String email; // Email đăng nhập
    private String name; // Tên hiển thị
    private String phone; // Số điện thoại (tùy chọn)
    private String profilePicture; // Đường dẫn ảnh đại diện
    private String role; // Vai trò: "attendee" hoặc "organizer"

    public User(String userId, String email, String phone, String name, String profilePicture, String role) {
        this.userId = userId;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setRole(String role) {
        this.role = role;
    }
}