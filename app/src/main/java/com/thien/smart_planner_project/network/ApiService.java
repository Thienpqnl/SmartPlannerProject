package com.thien.smart_planner_project.network;

import com.squareup.okhttp.ResponseBody;

import com.thien.smart_planner_project.model.NotificationItem;
import com.thien.smart_planner_project.model.SendNotificationRequest;
import com.thien.smart_planner_project.model.dto.ApiResponse;

import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.dto.ChatBoxDTO;
import com.thien.smart_planner_project.model.CheckinRequest;
import com.thien.smart_planner_project.model.CheckinResponse;
import com.thien.smart_planner_project.model.dto.EmailRequest;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.dto.FriendDTO;
import com.thien.smart_planner_project.model.InviteAddFriend;
import com.thien.smart_planner_project.model.Message;
import com.thien.smart_planner_project.model.dto.MarkReadRequest;
import com.thien.smart_planner_project.model.dto.SendMailInviteDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.dto.StatusFriend;
import com.thien.smart_planner_project.model.dto.StatusResponse;
import com.thien.smart_planner_project.model.dto.UserAttendeeDTO;

import java.util.List;
import java.util.Map;

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


    @POST("api/save-token")
    Call<ResponseBody> saveToken(@Body Map<String, String> payload);

    @POST("conservation/addFriend")
    Call<ApiResponse> addFriend(@Body InviteAddFriend invite);

    @POST("conservation/acceptFriend")
    Call<ApiResponse> acceptFriend(@Body InviteAddFriend invite);

    @POST("conservation/rejectFriend")
    Call<ApiResponse> rejectFriend(@Body InviteAddFriend invite);

    @POST("conservation/deleteFriend")
    Call<String> deleteFriend(@Body InviteAddFriend invite);

    @POST("conservation/blockFriend")
    Call<String> blockFriend(@Body InviteAddFriend invite);

    @POST("conservation/sendMessage")
    Call<ApiResponse> sendMessage(@Body Message message);

    @GET("conservation/listMessage/{friendId}/{page}/{size}")
    Call<List<Message>> listMessage(@Path("friendId") String friendId, @Path("page") int page, @Path("size") int size);

    @GET("conservation/listReceivedFriend/{userId}")
    Call<List<User>> listReceivedFriend(@Path("userId") String userId);

    @GET("conservation/listFriend/{userId}")
    Call<List<FriendDTO>> listFriend(@Path("userId") String userId);

    @GET("conservation/listChatBox/{userId}")
    Call<List<ChatBoxDTO>> listChatBox(@Path("userId") String userId);

    @POST("conservation/status/{userId}")
    Call<StatusResponse> getStatus(@Body StatusFriend statusFriend);

    @POST("conservation/setIsRead")
    Call<Void> setIsRead(@Body MarkReadRequest markReadRequest);

    @POST("api/send-notification")
    Call<Void> sendNotification(@Body SendNotificationRequest request);

    @GET("api/get-by-user/{userId}")
    Call<List<NotificationItem>> getNotificationsByUser(@Path("userId") String userId);

    @POST("api/save-notification")
    Call<Void> saveNotification(@Body NotificationItem item);
}