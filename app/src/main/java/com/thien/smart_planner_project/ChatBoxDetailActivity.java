package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.Adapter.MessageAdapter;
import com.thien.smart_planner_project.model.dto.ApiResponse;
import com.thien.smart_planner_project.model.Message;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.dto.MarkReadRequest;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;
import com.thien.smart_planner_project.service.SocketManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBoxDetailActivity extends AppCompatActivity {
    private List<Message> messages = new ArrayList<>();
    private RecyclerView recyclerView;
    private int page = 1;
    private int size = 20;
    private String friendId, uid;
    private MessageAdapter adapter;
    private boolean isLoadingOld = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_box_detail);

        ImageView img = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerView2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        img.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendId");
        uid = intent.getStringExtra("uid");
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        User user = SharedPrefManager.getInstance(this).getUser();

        // Load initial messages
        loadMessages(apiService);
        setIsRead(apiService, new MarkReadRequest(friendId, user.getUserId()));
        // Only load more messages if enough messages to scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoadingOld && !isLastPage
                        && layoutManager != null
                        && layoutManager.findFirstCompletelyVisibleItemPosition() == 0
                        && messages.size() >= size && dy < 0) {
                    loadOlderMessages(apiService);
                }
            }
        });

        EditText messageInput = findViewById(R.id.message_input);
        ImageView sendAction = findViewById(R.id.send_button);

        // Ban đầu disable nút gửi nếu chưa có text
        sendAction.setEnabled(false);
        sendAction.setAlpha(0.5f);

        // Lắng nghe thay đổi text để bật/tắt nút gửi
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s != null && s.toString().trim().length() > 0;
                sendAction.setEnabled(hasText);
                sendAction.setAlpha(hasText ? 1f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        sendAction.setOnClickListener(v -> {
            String content = messageInput.getText().toString().trim();
            if (content.isEmpty()) return;
            Message mes = new Message();
            mes.setFriendId(friendId);
            mes.setSender(user.getUserId());
            mes.setContent(content);

            Call<ApiResponse> call = apiService.sendMessage(mes);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        messageInput.setText("");
                        // Sau khi gửi thành công, load lại danh sách tin nhắn từ server
                        loadMessages(apiService);
                        SocketManager.getInstance().sendChatMessage(user.getName(),uid,"Gửi lời mời kết bạn");
                    } else {
                        String errorMsg = "Lỗi không xác định";
                        if (response.errorBody() != null) {
                            try {
                                String errorJson = response.errorBody().string();
                                JSONObject obj = new JSONObject(errorJson);
                                errorMsg = obj.optString("message", errorMsg);
                            } catch (Exception e) { }
                        }
                        Toast.makeText(v.getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });
    }

    private void setIsRead(ApiService apiService, MarkReadRequest markReadRequest) {
        Call<Void> call = apiService.setIsRead(markReadRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ChatBoxDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void loadMessages(ApiService apiService) {
        page = 1;
        isLastPage = false;
        Call<List<Message>> call = apiService.listMessage(friendId, page, size);
        call.enqueue(new Callback<List<Message>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.clear();
                    messages.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    if (response.body().size() < size) isLastPage = true;
                    // Sau khi load xong, scroll về cuối danh sách nếu có tin nhắn
                    if (!messages.isEmpty()) {
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                Toast.makeText(ChatBoxDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadOlderMessages(ApiService apiService) {
        isLoadingOld = true;
        page++; // Tăng số trang để lấy dữ liệu mới
        Call<List<Message>> call = apiService.listMessage(friendId, page, size);
        call.enqueue(new Callback<List<Message>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                isLoadingOld = false;
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    messages.addAll(0, response.body());
                    adapter.notifyDataSetChanged();
                    recyclerView.post(() -> {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.scrollToPositionWithOffset(response.body().size(), 0);
                        }
                    });
                    if (response.body().size() < size) isLastPage = true;
                } else {
                    isLastPage = true; // Không còn dữ liệu để tải nữa
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                isLoadingOld = false;
                Toast.makeText(ChatBoxDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}