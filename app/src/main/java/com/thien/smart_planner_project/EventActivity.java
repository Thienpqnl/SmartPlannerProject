package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thien.smart_planner_project.Adapter.EventAdapter;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private Button filterBtn;

    private EditText searchEdt;
    private User userLogin;
    private Toolbar toolbar;
    private ImageView imageView;

    private List<Event> eventList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_event);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        SessionManager sessionManager=new SessionManager(EventActivity.this);

        filterBtn = findViewById(R.id.btnFilter);
        searchEdt = findViewById(R.id.searchEdt);
        recyclerView = findViewById(R.id.recyclerViewEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);
        imageView=findViewById(R.id.bookings);

        String imageUrl = "https://cdn-icons-png.freepik.com/512/432/432312.png";
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
        imageView.setOnClickListener(v -> {
            startActivity(new Intent(EventActivity.this, BookingsActivity.class));
        });

        toolbar = findViewById(R.id.toolbarEvent);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sự kiện");
        toolbar.setNavigationOnClickListener(v -> finish());

        loadEvents();
        userLogin = (User) getIntent().getSerializableExtra("user");

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Event> newEvList = new ArrayList<>();
                  for (int i = 0; i < eventList.size(); i++) {
                      if (eventList.get(i).getName().toLowerCase().contains(searchEdt.getText().toString().toLowerCase())) {
                          newEvList.add(eventList.get(i));
                      }

                  }
                eventAdapter = new EventAdapter(newEvList);
                recyclerView.setAdapter(eventAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, FilterActivity.class);
                startActivityForResult(intent, 1001);
              //  startActivity(intent);
            }
        });
    }

    private void loadEvents() {
        if (!eventList.isEmpty()) {
            return;
        }
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getAllEvents();

        call.enqueue(new Callback<List<Event>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    for (Event ev : response.body()) {
//                        Log.d("Events: ",response.body().size()+"");
                        //Thiện_22130261 để isPresent là true nha
//                        if (!ev.isStatus()) {
//                            eventList.add(ev);
//                        }
                        if (ev.isStatus()) {
                            eventList.add(ev);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EventActivity.this, "Không lấy được sự kiện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(EventActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }


//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String fromDate = data.getStringExtra("fromDate");
            String toDate = data.getStringExtra("toDate");
            String fromTime = data.getStringExtra("fromTime");
            String toTime = data.getStringExtra("toTime");
            String eventType = data.getStringExtra("eventType");
            String location = data.getStringExtra("location");
            ArrayList<String> seatRanges = data.getStringArrayListExtra("seatRanges");

            // Gọi hàm lọc ở đây, ví dụ:
            handleFilter(fromDate, toDate, fromTime, toTime, eventType, location, seatRanges);
        }
    }

    //
    private void handleFilter(String fromDate, String toDate, String fromTime, String toTime,
                              String eventType, String location, List<String> seatRanges) {
        List<Event> filteredList = new ArrayList<>();

        for (Event event : eventList) {
            boolean match = true;

            // 1. Lọc theo ngày
            if (!fromDate.isEmpty() && event.getDate().compareTo(fromDate) < 0) match = false;
            if (!toDate.isEmpty() && event.getDate().compareTo(toDate) > 0) match = false;

            // 2. Lọc theo thời gian
            if (!fromTime.isEmpty() && event.getTime().compareTo(fromTime) < 0) match = false;
            if (!toTime.isEmpty() && event.getTime().compareTo(toTime) > 0) match = false;

            // 3. Lọc theo loại sự kiện
            if (!eventType.isEmpty() && !event.getType().equalsIgnoreCase(eventType)) match = false;

            // 4. Lọc theo địa điểm
            if (!location.isEmpty() && !event.getLocation().toLowerCase().contains(location.toLowerCase())) match = false;

            // 5. Lọc theo số ghế
            if (!seatRanges.isEmpty()) {
                boolean seatMatch = false;
                int seat = event.getSeats();

                for (String range : seatRanges) {
                    if (range.equals("1-10") && seat >= 1 && seat <= 10) seatMatch = true;
                    if (range.equals("11-50") && seat >= 11 && seat <= 50) seatMatch = true;
                    if (range.equals("51-100") && seat >= 51 && seat <= 100) seatMatch = true;
                    if (range.equals("101+") && seat > 100) seatMatch = true;
                }

                if (!seatMatch) match = false;
            }

            if (match) {
                filteredList.add(event);
            }
        }

        // Cập nhật RecyclerView
        eventAdapter = new EventAdapter(filteredList);
        recyclerView.setAdapter(eventAdapter);

        Toast.makeText(this, "Tìm thấy " + filteredList.size() + " sự kiện", Toast.LENGTH_SHORT).show();
    }



}
