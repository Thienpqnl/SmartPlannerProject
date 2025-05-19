package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thien.smart_planner_project.Adapter.OrganizerEventAdapter;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {


        super.onCreate(bundle);

        setContentView(R.layout.organizer_layout);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fab = findViewById(R.id.fab_add);
        ListView lsView = findViewById(R.id.listViewEvents);
        bottomAppBar.setNavigationOnClickListener(v -> {
            // Mo danh sach
        });

        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                // Mo trang profile
                return true;
            }
            return false;
        });

        fab.setOnClickListener(v -> {
            // Them moi
        });

        Intent intent = getIntent();

       String uid = intent.getStringExtra("uid");
        String role = intent.getStringExtra("role");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getOrganizerEventList(uid);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                assert response.body() != null;
                List<Event> eventList = new ArrayList<>(response.body());
                Log.e("log-loi", eventList.size() + "");
                OrganizerEventAdapter adapter = new OrganizerEventAdapter(OrganizerViewActivity.this, eventList, role);
                lsView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e ("err","loi khi goi api: "  + t);
            }
        });
    }
}
