package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.thien.smart_planner_project.Adapter.EventAdapter;
import com.thien.smart_planner_project.Adapter.EventAdapter1;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.UserAttendeeDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;
import com.thien.smart_planner_project.utils.InviteDialogUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPreviewActivity extends AppCompatActivity {
    private TextView name, email;
    private List<Event> eventJoinInPass;
    private EventAdapter1 adapter;
    private ImageView inviteImg;
    private User creator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        creator = SharedPrefManager.getInstance(this).getUser();
        eventJoinInPass = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter1(eventJoinInPass);
        recyclerView.setAdapter(adapter);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        inviteImg = findViewById(R.id.invite_image);
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserById(uid);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
               if(response.body() == null){
                   Toast.makeText(UserPreviewActivity.this, "không tìm thấy thông tin của người dùng này", Toast.LENGTH_SHORT).show();
                   return;
               }
               User user = response.body();
               name.setText(user.getName());
               email.setText(user.getEmail());

                Call<List<Event>> call1 = apiService.getListEventUserHasCheckIn(uid);
                call1.enqueue(new Callback<List<Event>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(UserPreviewActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(response.body() == null || response.body().isEmpty()){
                            Toast.makeText(UserPreviewActivity.this,"Kết quả nhận được là rỗng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        eventJoinInPass.clear();
                        eventJoinInPass.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(UserPreviewActivity.this,String.format("Danh sách các sự kiện đã tham gia của %s",user.getName()), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                        Toast.makeText(UserPreviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(UserPreviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        inviteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

                Call<List<Event>> call = apiService.eventInviteEligible(creator.getUserId(),uid);
                call.enqueue(new Callback<List<Event>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(UserPreviewActivity.this, "Không lấy được danh sách", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(response.body() == null || response.body().isEmpty()){
                            Toast.makeText(UserPreviewActivity.this, "Danh sách sự kiện rỗng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        response.body().forEach(System.out::println);
                        InviteDialogUtil inviteDialogUtil = new InviteDialogUtil(UserPreviewActivity.this,response.body(),email.getText().toString());
                        inviteDialogUtil.showInviteDialog();
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                        Toast.makeText(UserPreviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}