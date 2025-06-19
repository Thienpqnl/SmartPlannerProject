package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private Button filterBtn, btnSearchID;

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
        btnSearchID = findViewById(R.id.btnsearchID);
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






        btnSearchID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = searchEdt.getText().toString().trim();
                if (id.isEmpty()) {
                    Toast.makeText(EventActivity.this, "Vui lòng nhập ID sự kiện!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiService apiService1 = RetrofitClient.getClient().create(ApiService.class);
                apiService1.getEventByIdEvent(id).enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Event event = response.body();
                            openEventDetail(event); // Gọi hàm mở chi tiết
                        } else {
                            Toast.makeText(EventActivity.this, "Không tìm thấy sự kiện!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        Toast.makeText(EventActivity.this, "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                    }
                });
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


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
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

                        if (ev.isStatus()) {// Nếu sự kiện chưa diễn ra
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

        Date fromDateObj = parseDate(fromDate);
        Date toDateObj = parseDate(toDate);

        Date fromTimeObj = parseTime(fromTime);
        Date toTimeObj = parseTime(toTime);

        for (Event event : eventList) {
            if(!event.isStatus())continue;
            // 1. Lọc theo ngày
            Date eventDateObj = parseDate(event.getDate());
            if (fromDateObj != null && eventDateObj != null && eventDateObj.before(fromDateObj)) {
                continue;
            }
            if (toDateObj != null && eventDateObj != null && eventDateObj.after(toDateObj)) {
                continue;
            }

            // 2. Lọc theo giờ
            Date eventTimeObj = parseTime(event.getTime());
            if (fromTimeObj != null && eventTimeObj != null && eventTimeObj.before(fromTimeObj)) {
                continue;
            }
            if (toTimeObj != null && eventTimeObj != null && eventTimeObj.after(toTimeObj)) {
                continue;
            }

            // 3. Lọc theo loại sự kiện
            if (eventType != null && !eventType.isEmpty() && !event.getType().equalsIgnoreCase(eventType)) {
                continue;
            }

            // 4. Lọc theo địa điểm
            if (location != null && !location.isEmpty()) {
                String eventLocation = event.getLocation() == null ? "" : removeVietnameseTones(event.getLocation().toLowerCase());
                String filterLocation = removeVietnameseTones(location.toLowerCase());
                if (!eventLocation.contains(filterLocation)) {
                    continue;
                }
            }

            // 5. Lọc theo số ghế
            boolean seatValid = seatRanges == null || seatRanges.isEmpty();
            int seat = event.getSeats();
            if (seatRanges != null && !seatRanges.isEmpty()) {
                for (String range : seatRanges) {
                    switch (range) {
                        case "1-10":
                            if (seat >= 1 && seat <= 10) seatValid = true;
                            break;
                        case "11-50":
                            if (seat >= 11 && seat <= 50) seatValid = true;
                            break;
                        case "51-100":
                            if (seat >= 51 && seat <= 100) seatValid = true;
                            break;
                        case "101+":
                            if (seat >= 101) seatValid = true;
                            break;
                    }
                }
            }
            if (!seatValid) continue;

            // Nếu qua hết các điều kiện thì add vào filteredList
            filteredList.add(event);
        }

        // Cập nhật adapter để hiển thị kết quả lọc
        eventAdapter = new EventAdapter(filteredList);
        recyclerView.setAdapter(eventAdapter);

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Không có kết quả phù hợp", Toast.LENGTH_SHORT).show();
        }
    }


    //chuyển đổi ngày giờ từ String thành date
    public Date parseDateTime(String dateStr, String timeStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dateStr + " " + timeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public Date parseTime(String timeStr) {
        try {
            return new SimpleDateFormat("HH:mm").parse(timeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String removeVietnameseTones(String str) {
        str = str.replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a");
        str = str.replaceAll("[ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬ]", "A");
        str = str.replaceAll("[éèẻẽẹêếềểễệ]", "e");
        str = str.replaceAll("[ÉÈẺẼẸÊẾỀỂỄỆ]", "E");
        str = str.replaceAll("[íìỉĩị]", "i");
        str = str.replaceAll("[ÍÌỈĨỊ]", "I");
        str = str.replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o");
        str = str.replaceAll("[ÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢ]", "O");
        str = str.replaceAll("[úùủũụưứừửữự]", "u");
        str = str.replaceAll("[ÚÙỦŨỤƯỨỪỬỮỰ]", "U");
        str = str.replaceAll("[ýỳỷỹỵ]", "y");
        str = str.replaceAll("[ÝỲỶỸỴ]", "Y");
        str = str.replaceAll("đ", "d");
        str = str.replaceAll("Đ", "D");
        // Optionally remove accent-like symbols
        str = str.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return str;
    }


    private void openEventDetail(Event event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("id", event.getId());
        intent.putExtra("name", event.getName());
        intent.putExtra("time", event.getTime());
        intent.putExtra("location", event.getLocation());
        intent.putExtra("seat", event.getSeats());
        intent.putExtra("des", event.getDescription());
        intent.putExtra("date", event.getDate());
        intent.putExtra("image", event.getImageUrl());
        intent.putExtra("uid", event.getId());
        startActivity(intent);
    }

}
