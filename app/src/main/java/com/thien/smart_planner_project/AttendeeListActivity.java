package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeListActivity extends AppCompatActivity {
    private final List<UserAttendeeDTO> userAttendeeDTOS = new ArrayList<>();
    private final List<UserAttendeeDTO> originalList = new ArrayList<>();
    private AttendeeAdapter adapter;
    private TextView amount, title;
    private SearchView search;
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
        search = findViewById(R.id.search_attendee);
        amount = findViewById(R.id.amount_attendee);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        adapter = new AttendeeAdapter(userAttendeeDTOS,eventId, "Danh sách người đặt vé");
        recyclerView.setAdapter(adapter);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<UserAttendeeDTO>> call = apiService.getListRegisEvent(eventId);
        getListAttendee(call,eventId);
        Spinner spinner = findViewById(R.id.list_spinner);
        List<String> list = Arrays.asList("Danh sách người đặt vé", "Danh sách người tham gia", "Danh sách hạn chế");

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
                Call<List<UserAttendeeDTO>> call = null;
                if("Danh sách người đặt vé".equals(item)){
                    adapter.setType(item);
                    call = apiService.getListRegisEvent(eventId);
                }
                if("Danh sách hạn chế".equals(item)){
                    adapter.setType(item);
                    call = apiService.getListRestricedUserInEvent(eventId);
                }
                if("Danh sách người tham gia".equals(item)){
                    adapter.setType(item);
                    call = apiService.getListAttendeeHasCheckInEvent(eventId);
                }
                title.setText(item);
                getListAttendee(call,eventId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        adapter.setOnCountChangeListener(new AttendeeAdapter.OnCountChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCountChanged(int count) {
                amount.setText(String.format("Số lượng: %d", count));
            }
        });
    }
    private void getListAttendee(Call<List<UserAttendeeDTO>> call, String eventId){
        call.enqueue(new Callback<List<UserAttendeeDTO>>() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<List<UserAttendeeDTO>> call, @NonNull Response<List<UserAttendeeDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userAttendeeDTOS.clear();
                    originalList.clear();
                    if(response.body().isEmpty()){
                        Toast.makeText(AttendeeListActivity.this, "Danh sách rỗng", Toast.LENGTH_SHORT).show();
                    }else{
                        userAttendeeDTOS.addAll(response.body());
                        originalList.addAll(response.body());
                    }
                    amount.setText(String.format("Số lượng: %d", userAttendeeDTOS.size()));
                    adapter.notifyDataSetChanged();
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
    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    public void filter(String newText) {
        if(newText == null || newText.isEmpty()){
            userAttendeeDTOS.clear();
            userAttendeeDTOS.addAll(originalList);
        }else {
            userAttendeeDTOS.clear();
            userAttendeeDTOS.addAll(originalList.stream()
                    .filter(u -> u.getUser().getName().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        amount.setText(String.format("Số lượng: %d", userAttendeeDTOS.size()));
        adapter.notifyDataSetChanged();
    }
}