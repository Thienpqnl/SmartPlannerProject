package com.thien.smart_planner_project.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thien.smart_planner_project.Adapter.ChatBoxAdapter;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.dto.ChatBoxDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatBoxFragment extends Fragment {
    private List<ChatBoxDTO> listChat;

    public ChatBoxFragment() {
        listChat = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_box, container, false);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        RecyclerView recyclerView = view.findViewById(R.id.chat_box_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ChatBoxAdapter adapter = new ChatBoxAdapter(listChat);
        recyclerView.setAdapter(adapter);
        Call<List<ChatBoxDTO>> call = apiService.listChatBox(user.getUserId());
        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ChatBoxDTO>> call, @NonNull Response<List<ChatBoxDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                   listChat.clear();
                   listChat.addAll(response.body());
                   adapter.notifyDataSetChanged();
                } else {
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
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatBoxDTO>> call, @NonNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}