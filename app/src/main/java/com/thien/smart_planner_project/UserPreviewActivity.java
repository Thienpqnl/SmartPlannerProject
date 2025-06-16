package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.thien.smart_planner_project.Adapter.EventAdapter1;
import com.thien.smart_planner_project.model.dto.ApiResponse;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.InviteAddFriend;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.dto.StatusFriend;
import com.thien.smart_planner_project.model.dto.StatusResponse;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;
import com.thien.smart_planner_project.service.SocketManager;
import com.thien.smart_planner_project.utils.InviteDialogUtil;

import org.json.JSONObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPreviewActivity extends AppCompatActivity {
    private TextView name, email;
    private List<Event> eventJoinInPass;
    private EventAdapter1 adapter;
    private ImageView inviteImg, addFriend;
    private User creator;
    private ApiService apiService;

    public UserPreviewActivity() {
        apiService = RetrofitClient.getClient().create(ApiService.class);
    }

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
        addFriend = findViewById(R.id.addFriend);
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");

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
        inviteImg.setOnClickListener(v -> {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

            Call<List<Event>> call3 = apiService.eventInviteEligible(creator.getUserId(),uid);
            call3.enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(@NonNull Call<List<Event>> call3, @NonNull Response<List<Event>> response) {
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
                public void onFailure(@NonNull Call<List<Event>> call3, @NonNull Throwable t) {
                    Toast.makeText(UserPreviewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        addFriend.setOnClickListener(v -> {
            Call<ApiResponse> call2 = apiService.addFriend(new InviteAddFriend(creator.getUserId(),uid));
            call2.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Thành công
                        Toast.makeText(v.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        SocketManager.getInstance().sendFriendRequest(creator.getName(),uid,"Gửi lời mời kết bạn");
                    } else {
                        // Trường hợp lỗi, cần tự parse errorBody
                        String errorMsg = "Lỗi không xác định";
                        if (response.errorBody() != null) {
                            try {
                                String errorJson = response.errorBody().string();
                                JSONObject obj = new JSONObject(errorJson);
                                errorMsg = obj.optString("message", errorMsg);
                            } catch (Exception e) {
                                // Có thể log lỗi parse JSON ở đây
                            }
                        }
                        Toast.makeText(v.getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
        User user = SharedPrefManager.getInstance(UserPreviewActivity.this).getUser();
        StatusResponse statusResponse = getStatusFriend(user.getUserId(),uid);
        if(!(statusResponse.getStatusFrom().equals("accepted") && statusResponse.getStatusTo().equals("accepted"))){
            addFriend.setVisibility(View.INVISIBLE);
        }
    }
    private StatusResponse getStatusFriend(String from, String to){
        final StatusResponse[] res = new StatusResponse[1];
        Call<StatusResponse> call = apiService.getStatus(new StatusFriend(from,to));
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    res[0] = response.body();
                }else{
                    String errorMsg = "Lỗi không xác định";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            JSONObject obj = new JSONObject(errorJson);
                            errorMsg = obj.optString("message", errorMsg);
                        } catch (Exception e) {
                            // Log error if needed
                        }
                    }
                    Toast.makeText(UserPreviewActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(UserPreviewActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return res[0];
    }
}