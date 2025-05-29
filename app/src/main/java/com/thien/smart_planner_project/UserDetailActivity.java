package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thien.smart_planner_project.Adapter.OrganizerEventAdapter;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends AppCompatActivity {

    TextView textViewRole, organizerName, textViewEmail, textViewlocation;
    User user;
    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);

        setContentView(R.layout.user_detail_layout);

        textViewRole = findViewById(R.id.textViewRole);
        textViewEmail = findViewById(R.id.textViewEmail);
        organizerName = findViewById(R.id.organizerName);
        textViewlocation = findViewById(R.id.textViewAddress);
        Intent intent = getIntent();

        organizerName.setText(intent.getStringExtra("name"));
        textViewEmail.setText(intent.getStringExtra("email"));
        textViewRole.setText(intent.getStringExtra("role"));
        textViewlocation.setText(intent.getStringExtra("local"));
        ListView lsView = findViewById(R.id.listViewEvents);
        String uid = intent.getStringExtra("uid");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getOrganizerEventList(uid);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                assert response.body() != null;
                List<Event> eventList = new ArrayList<>();
                for (Event ev : response.body()) {
                    if (!ev.isStatus()) {
                        eventList.add(ev);
                    }
                }
                Log.e("log-loi", eventList.size() + "");
                OrganizerEventAdapter adapter = new OrganizerEventAdapter(UserDetailActivity.this, eventList, intent.getStringExtra("role"));
                lsView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e ("err","loi khi goi api: "  + t);
            }
        });
    }
}
