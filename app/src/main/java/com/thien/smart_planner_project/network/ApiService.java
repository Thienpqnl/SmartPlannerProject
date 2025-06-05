package com.thien.smart_planner_project.network;

import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.CheckinRequest;
import com.thien.smart_planner_project.model.CheckinResponse;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("events/users/{uid}")
    Call<List<Event>> getOrganizerEventList(@Path("uid") String userId);
    @GET("events/{idEvent}")
    Call<Event>getEventByIdEvent(@Path("idEvent") String idEvent);
    @PUT("events/{eventId}")
    Call<Event> updateEvent(@Path("eventId") String eventId, @Body Event updatedEvent);
    //booking
    @POST("bookings/")
    Call<Booking> createBooking(@Body Booking bookingRequest);
    @GET("bookings/user/{userId}")
    Call<List<Booking>>getBookings(@Path("userId") String userId);
    @DELETE("bookings/{idBooking}")
    Call<List<Booking>>deleteBooking(@Path("idBooking") String idBooking);

    @GET("attendee/getListByEvent/{eventId}")
    Call<List<User>> getListRegisEvent(@Path("eventId") String eventId);

    @POST("/attendees/checking")
    Call<CheckinResponse> checkIn(@Body CheckinRequest request);
}