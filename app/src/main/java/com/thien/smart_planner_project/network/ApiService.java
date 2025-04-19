package com.thien.smart_planner_project.network;

import com.thien.smart_planner_project.model.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/events")
    Call<Event> createEvent(@Body Event event);

    @GET("/events")
    Call<List<Event>> getAllEvents();
}