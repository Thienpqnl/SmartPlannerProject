package com.thien.smart_planner_project.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {

    private String name;
    private String date;
    private String time;
    private String id;
    private  String creatorUid;
    private String location;
    private String description;
    private int seats;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private  String type;

    private  boolean isPresent;
    @SerializedName("_id") // dùng nếu bạn dùng Gson
    private String _id;
    //name, date, location,  time, selectedItem,description, uploadedImageUrl, seats, longitude, latitude, createUser.getUserId())
    public Event(String name, String date, String location ,String time, String type,String description, String imageUrl, int seats, double longitude, double latitude, String creatorUid) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.time = time;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.seats = seats;
        this.longitude = longitude;
        this.latitude = latitude;
        this.creatorUid = creatorUid;
    }

    public boolean isStatus() {
        return isPresent;
    }

    public void setStatus(boolean status) {
        this.isPresent = status;
    }

    public String getName() {
        return name;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getId() {
        return id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getSeats() {
        return seats;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setImageUrl(String imageURL) {
        this.imageUrl = imageURL;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
