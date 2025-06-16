package com.thien.smart_planner_project.network;

import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.CheckinRequest;
import com.thien.smart_planner_project.model.CheckinResponse;
import com.thien.smart_planner_project.model.EmailRequest;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.SendMailInviteDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.UserAttendeeDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("attendees/getListByEvent/{eventId}")
    Call<List<UserAttendeeDTO>> getListRegisEvent(@Path("eventId") String eventId);

    @POST("/attendees/checking")
    Call<CheckinResponse> checkIn(@Body CheckinRequest request);

    @GET("attendees/getListEventUserHasCheckIn/{userId}")
    Call<List<Event>> getListEventUserHasCheckIn(@Path("userId") String userID);

    @GET("attendees/getListAttendeeHasCheckInEvent/{eventId}")
    Call<List<UserAttendeeDTO>> getListAttendeeHasCheckInEvent(@Path("eventId") String eventId);

    @DELETE("attendees/deleteAttendee/{eventId}/{userId}")
    Call<Void> deleteAttendee(@Path("userId") String userId, @Path("eventId") String eventId);

    @POST("attendees/sendEmailAboutDeteleBookTicket")
    Call<Void> sendEmailAboutDeteleBookTicket(@Body EmailRequest emailRequest);

    @POST("attendees/sendEmailInvite")
    Call<Void> sendEmailInvite(@Body EmailRequest emailRequest);

    @GET("attendees/eventInviteEligible/{creatorId}/{userId}")
    Call<List<Event>> eventInviteEligible(@Path("creatorId") String creatorId, @Path("userId") String userId);

    @POST("attendees/sendEmailInvite")
    Call<Void> sendEmailInvites(@Body SendMailInviteDTO sendMailInviteDTO);

    @GET("attendees/getListRestricedUserInEvent/{eventId}")
    Call<List<UserAttendeeDTO>> getListRestricedUserInEvent(@Path("eventId") String eventId);

    @GET("attendees/putUserInRestrictedList/{eventId}/{userId}/")
    Call<Void> putUserInRestrictedList(@Path("eventId") String eventId ,@Path("userId") String userId);

    @GET("attendees/removeUserFromRestrictedList/{eventId}/{userId}/")
    Call<Void> removeUserFromRestrictedList(@Path("eventId") String eventId ,@Path("userId") String userId);
}