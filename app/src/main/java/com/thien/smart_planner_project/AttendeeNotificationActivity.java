package com.thien.smart_planner_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.Adapter.NotificationAdapter;
import com.thien.smart_planner_project.model.NotificationItem;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        String userId = getCurrentUserId(); // Lấy userId từ Intent hoặc Session
        loadNotificationsFromServer(userId);
    }

    private void loadNotificationsFromServer(String userId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<NotificationItem>> call = apiService.getNotificationsByUser(userId);

        call.enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                Toast.makeText(AttendeeNotificationActivity.this, "Lỗi tải thông báo từ server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentUserId() {
        // Ví dụ: lấy từ Intent hoặc SessionManager
        return getIntent().getStringExtra("userId");
    }
}