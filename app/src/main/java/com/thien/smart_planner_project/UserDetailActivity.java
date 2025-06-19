package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.thien.smart_planner_project.Adapter.OrganizerEventAdapter;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

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

        user = SharedPrefManager.getInstance(this).getUser();
        if(user == null){
            Intent i = new Intent(UserDetailActivity.this, LoginActivity.class);
            return;
        }
        organizerName.setText(user.getName());
        textViewEmail.setText(user.getEmail());
        textViewRole.setText(user.getRole());
        textViewlocation.setText(user.getLocation());
        ListView lsView = findViewById(R.id.listViewEvents);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getOrganizerEventList(user.getUserId());

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                assert response.body() != null;
                List<Event> eventList = new ArrayList<>(response.body());
                Log.e("log-loi", eventList.size() + "");
                OrganizerEventAdapter adapter = new OrganizerEventAdapter(UserDetailActivity.this, eventList, "attendee");
                lsView.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                Log.e ("err","loi khi goi api: "  + t);
            }
        });

        ImageView userDetailBack = findViewById(R.id.user_detail_back);
        userDetailBack.setOnClickListener(v ->{
            finish();
        });

        LinearLayout friends = findViewById(R.id.user_detail_friends);
        friends.setOnClickListener(v ->{
            Intent i = new Intent(UserDetailActivity.this, MainChatActivity.class);
            startActivity(i);
        });
    }
}
