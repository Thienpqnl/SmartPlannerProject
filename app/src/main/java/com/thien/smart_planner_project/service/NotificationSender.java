package com.thien.smart_planner_project.service;

import android.content.Context;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.model.SendNotificationRequest;

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
                    // Thông báo đã được gửi đến server
                } else {
                    // Có thể log lỗi từ server ở đây
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Log lỗi mạng hoặc server không phản hồi
            }
        });
    }
}