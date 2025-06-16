package com.thien.smart_planner_project.model;

import java.io.Serializable;

public class User implements Serializable{
    private String userId;
    private String email;
    private String name;
    private String location;
    private String role;
    private double longitude;
    private double latitude;

    public User() {
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
    public User(String userId, String email,  String name, String location ,String role, double longitude, double latitude) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.location = location;
        this.role = role;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public User(String email,  String name, String location, String role, double longitude, double latitude) {

        this.email = email;
        this.name = name;
        this.location = location;
        this.role = role;
        this.longitude = longitude;
        this.latitude = latitude;
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



    public String getRole() {
        return role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
}