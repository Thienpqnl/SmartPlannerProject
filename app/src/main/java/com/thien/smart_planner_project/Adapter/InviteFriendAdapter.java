package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.ChatBoxDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.dto.ApiResponse;
import com.thien.smart_planner_project.model.InviteAddFriend;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendAdapter.InviteFriendViewHolder> {
    private static List<User> inviteFriend;

    public InviteFriendAdapter(List<User> inviteFriend) {
        this.inviteFriend = inviteFriend;
    }

    public static class InviteFriendViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView avatar, accept, reject;

        public InviteFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.friend_name);
            accept = itemView.findViewById(R.id.accept_add_friend);
            reject = itemView.findViewById(R.id.reject_add_friend);
            avatar = itemView.findViewById(R.id.friend_image_invite);

        }
    }

    @NonNull
    @Override
    public InviteFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new InviteFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteFriendViewHolder holder, int position) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        User friend = inviteFriend.get(position);
        holder.txtName.setText(friend.getName());
        holder.avatar.setOnClickListener(v -> {
            User user = inviteFriend.get(position);
            User userLogin = SharedPrefManager.getInstance(v.getContext()).getUser();
            Call<ApiResponse> call = apiService.acceptFriend(new InviteAddFriend(user.getUserId(),userLogin.getUserId()));
            call.enqueue(new Callback<ApiResponse>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        inviteFriend.remove(user);
                        notifyDataSetChanged();
                        Toast.makeText(v.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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
        holder.reject.setOnClickListener(v -> {
            User user = inviteFriend.get(position);
            User userLogin = SharedPrefManager.getInstance(v.getContext()).getUser();
            Call<ApiResponse> call = apiService.rejectFriend(new InviteAddFriend(user.getUserId(),userLogin.getUserId()));
            call.enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Thành công
                        inviteFriend.remove(user);
                        notifyDataSetChanged();
                        Toast.makeText(v.getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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
    }

    @Override
    public int getItemCount() {
        return inviteFriend.size();
    }
}
