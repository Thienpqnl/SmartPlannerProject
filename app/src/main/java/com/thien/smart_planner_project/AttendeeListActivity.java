package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.thien.smart_planner_project.model.UserAttendeeDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeListActivity extends AppCompatActivity {
    private final List<UserAttendeeDTO> userAttendeeDTOS = new ArrayList<>();
    private AttendeeAdapter adapter;
    TextView amount, title;
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
        title = findViewById(R.id.title_attendee_list);
        title.setText(getIntent().getStringExtra("title"));

        amount = findViewById(R.id.amount_attendee);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        adapter = new AttendeeAdapter(userAttendeeDTOS,eventId, true);
        recyclerView.setAdapter(adapter);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<UserAttendeeDTO>> call = apiService.getListRegisEvent(eventId);
        getListAttendee(call,eventId);
        Spinner spinner = findViewById(R.id.list_spinner);
        List<String> list = Arrays.asList("Danh sách người đặt vé", "Danh sách người tham gia");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                list
        );

        arrayAdapter.setDropDownViewResource(R.layout.spinner_item); // style cho dropdown
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Call<List<UserAttendeeDTO>> call;
                if("Danh sách người đặt vé".equals(item)){
                    adapter.setBooking(true);
                    call = apiService.getListRegisEvent(eventId);
                }else{
                    adapter.setBooking(false);
                    call = apiService.getListAttendeeHasCheckInEvent(eventId);

                }
                getListAttendee(call,eventId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });
    }
    private void getListAttendee(Call<List<UserAttendeeDTO>> call, String eventId){
        call.enqueue(new Callback<List<UserAttendeeDTO>>() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<List<UserAttendeeDTO>> call, @NonNull Response<List<UserAttendeeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if(response.body().isEmpty()){
                        Toast.makeText(AttendeeListActivity.this, "Danh sách rỗng", Toast.LENGTH_SHORT).show();
                    }else{
                        userAttendeeDTOS.clear();
                        userAttendeeDTOS.addAll(response.body());
                        amount.setText(String.format("Số lượng %d:", userAttendeeDTOS.size()));

                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(AttendeeListActivity.this, "Không lấy được danh sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserAttendeeDTO>> call, @NonNull Throwable t) {
                Toast.makeText(AttendeeListActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}