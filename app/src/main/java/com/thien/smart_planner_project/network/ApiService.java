package com.thien.smart_planner_project.network;

import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/events")
    Call<Event> createEvent(@Body Event event);

    @GET("/events")
    Call<List<Event>> getAllEvents();

    @POST("/api/users/register-or-update")
    Call<User> createUser(@Body User user);

    @GET("api/users/{uid}")
    Call<User> getUserById(@Path("uid") String userId);
}