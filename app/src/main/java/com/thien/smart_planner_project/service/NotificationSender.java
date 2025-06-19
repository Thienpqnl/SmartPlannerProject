package com.thien.smart_planner_project.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.thien.smart_planner_project.model.NotificationItem;
import com.thien.smart_planner_project.model.dto.UserAttendeeDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.model.SendNotificationRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationSender {

    public static void sendNotification(Context context, String userId, String title, String body, String type) {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        SendNotificationRequest request = new SendNotificationRequest(userId, title, body, type);

        Call<Void> call = apiService.sendNotification(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NOTI", "Thông báo đã được gửi đến server");

                    // Sau khi gửi thành công, lưu vào server
                    String timestamp = System.currentTimeMillis() + "";
                    String notificationId = "notif_" + timestamp;

                    NotificationItem notificationItem = new NotificationItem(
                            userId,
                            title,
                            body,
                            type,
                            timestamp
                    );

                    saveNotificationToServer(context, notificationItem);
                } else {
                    Toast.makeText(context, "Không thể gửi thông báo tới userId: " + userId, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối tới server", Toast.LENGTH_SHORT).show();
                Log.e("NOTI", "Lỗi gửi thông báo", t);
            }
        });
    }
    public static void sendNotificationToAllEventAttendees(
            Context context,
            String eventId,
            String title,
            String body,
            String type) {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<List<UserAttendeeDTO>> call = apiService.getListRegisEvent(eventId);
        call.enqueue(new Callback<List<UserAttendeeDTO>>() {
            @Override
            public void onResponse(Call<List<UserAttendeeDTO>> call, Response<List<UserAttendeeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserAttendeeDTO> attendees = response.body();

                    for (UserAttendeeDTO attendee : attendees) {
                        String userId = attendee.getUser().getUserId();

                        // Gọi API để gửi thông báo cho từng userId này
                        NotificationSender.sendNotification(context, userId, title, body, type);
                    }

                    Toast.makeText(context, "Thông báo đã bắt đầu gửi", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Lỗi khi lấy danh sách người tham gia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserAttendeeDTO>> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối tới server", Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Lỗi gọi API lấy danh sách người tham gia: " + t.getMessage());
            }
        });
    }
    private static void saveNotificationToServer(Context context, NotificationItem notificationItem) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.saveNotification(notificationItem);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NOTI", "Thông báo đã được lưu");
                } else {
                    Log.e("NOTI", "Lỗi khi lưu thông báo");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NOTI", "Lỗi lưu thông báo lên server", t);
            }
        });
    }
}