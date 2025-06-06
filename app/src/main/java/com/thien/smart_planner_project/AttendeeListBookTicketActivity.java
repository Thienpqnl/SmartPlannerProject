package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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
import com.thien.smart_planner_project.model.UserBookingDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeListBookTicketActivity extends AppCompatActivity {
    private final List<UserBookingDTO> userBookingDTOS = new ArrayList<>();
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
        TextView title = findViewById(R.id.title_attendee_list);
        TextView amount = findViewById(R.id.amount_attendee);
        title.setText(getIntent().getStringExtra("title"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        adapter = new AttendeeAdapter(userBookingDTOS,eventId);
        recyclerView.setAdapter(adapter);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        try {
            Call<List<UserBookingDTO>> call = apiService.getListRegisEvent(eventId);
            call.enqueue(new Callback<List<UserBookingDTO>>() {
                @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
                @Override
                public void onResponse(@NonNull Call<List<UserBookingDTO>> call, @NonNull Response<List<UserBookingDTO>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                       if(response.body().isEmpty()){
                           Toast.makeText(AttendeeListBookTicketActivity.this, "Chưa có ai đặt vé cho sự kiện này", Toast.LENGTH_SHORT).show();

                       }else{
                           userBookingDTOS.clear();
                           userBookingDTOS.addAll(response.body());
                           amount.setText(String.format("Số lượng %d:",userBookingDTOS.size()));
                           adapter.notifyDataSetChanged();
                       }
                    } else {
                        Toast.makeText(AttendeeListBookTicketActivity.this, "Không lấy được danh sách người đặt vé", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<UserBookingDTO>> call, @NonNull Throwable t) {
                    Toast.makeText(AttendeeListBookTicketActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}