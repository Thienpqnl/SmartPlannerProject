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

import com.thien.smart_planner_project.Adapter.InviteFriendAdapter;
import com.thien.smart_planner_project.R;
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

public class InviteFriendFragment extends Fragment {
    private final List<User> inviteFriend = new ArrayList<>();
    private InviteFriendAdapter adapter;

    public InviteFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        RecyclerView recyclerView = view.findViewById(R.id.invite_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new InviteFriendAdapter(inviteFriend);
        recyclerView.setAdapter(adapter);

        if (user != null) {
            Call<List<User>> call = apiService.listReceivedFriend(user.getUserId());
            call.enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        inviteFriend.clear();
                        inviteFriend.addAll(response.body());
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
                public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        return view;
    }
}