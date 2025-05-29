package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.Adapter.AttendeeAdapter;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeListActivity extends AppCompatActivity {
    private List<User> users = new ArrayList<>();
    private RecyclerView recyclerView;
    private AttendeeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendee_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendeeAdapter(users);
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        try {
            Call<List<User>> call = apiService.getListRegisEvent(eventId);
            call.enqueue(new Callback<List<User>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        System.out.println(response.body().size());
                        System.out.println(123);
                        users.clear();
                        users.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AttendeeListActivity.this, "Không lấy được danh sách người đặt vé", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                    Toast.makeText(AttendeeListActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}