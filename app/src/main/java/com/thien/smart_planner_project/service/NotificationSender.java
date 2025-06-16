package com.thien.smart_planner_project.service;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import com.thien.smart_planner_project.model.MySingleton;

public class NotificationSender {

    private static final String SEND_NOTIFICATION_URL = "http://172.17.114.181:3000/api/send-notification";

    public static void sendNotification(Context context, String userId, String title, String body, String type) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", userId);
            jsonBody.put("title", title);
            jsonBody.put("body", body);
            jsonBody.put("type", type);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    SEND_NOTIFICATION_URL,
                    jsonBody,
                    response -> {
                        // Có thể log hoặc bỏ qua nếu không cần phản hồi cụ thể
                    },
                    error -> {
                        // Xử lý lỗi ở đây nếu cần
                    }
            );

            MySingleton.getInstance(context).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}