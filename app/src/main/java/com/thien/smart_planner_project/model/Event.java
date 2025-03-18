package com.thien.smart_planner_project.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {
    private String name;
    private String date;
    private String time;
    private String address;
    private String seatCount;
    private String imagePath;
    private  String des;
    public Event(String name, String date, String time, String address, String seatCount, String imagePath, String des) {
        this.name = name;
        this.date = date;
        this.time = time;
       this.address = address;
        this.seatCount = seatCount;
        this.imagePath = imagePath;
        this.des = des;
    }


    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getAddress() { return address; }
    public String getSeatCount() { return seatCount; }
    public String getImagePath() { return imagePath; }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("date", date);
        data.put("time", time);
        data.put("address", address);
        data.put("seatCount", seatCount);
        data.put("imagePath", imagePath);
        return data;
    }
}
