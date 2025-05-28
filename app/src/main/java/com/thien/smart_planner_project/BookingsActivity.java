package com.thien.smart_planner_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thien.smart_planner_project.Adapter.BookingAdapter;
import com.thien.smart_planner_project.callback.ApiCallback;
import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.List;
import java.util.Objects;

public class BookingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookings);

        listView=findViewById(R.id.listBooking);
        toolbar = findViewById(R.id.toolbarBooking);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Danh sách vé");
        toolbar.setNavigationOnClickListener(v -> finish());
        //lấy session
        SessionManager sessionManager=new SessionManager(BookingsActivity.this);

        getBookings(sessionManager.getUserId());

    }
    private void getBookings(String userId){
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getBookings(userId).enqueue(new ApiCallback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> result) {
                BookingAdapter adapter=new BookingAdapter(BookingsActivity.this,result);
                listView.setAdapter(adapter);
//                Log.d("Bookings", "Bookings size: " + result.get(0).toString());

            }
        });
    }
}
