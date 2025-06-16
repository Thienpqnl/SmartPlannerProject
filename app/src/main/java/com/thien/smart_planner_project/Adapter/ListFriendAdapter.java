package com.thien.smart_planner_project.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.ChatBoxDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.dto.FriendDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.util.List;

public class ListFriendAdapter extends RecyclerView.Adapter<ListFriendAdapter.listFriendViewHolder> {
    private static List<FriendDTO> listFriend;

    public ListFriendAdapter(List<FriendDTO> listFriend) {
        this.listFriend = listFriend;
    }

    public static class listFriendViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView avatar, accept, reject;

        public listFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.friend_name);
            accept = itemView.findViewById(R.id.accept_add_friend);
            reject = itemView.findViewById(R.id.reject_add_friend);
            avatar = itemView.findViewById(R.id.friend_image_invite);
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            User user = SharedPrefManager.getInstance(itemView.getContext()).getUser();
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(v.getContext(), ChatBoxDetailActivity.class);

                    intent.putExtra("friendId",listFriend.get(position).getFriendId());
                    intent.putExtra("uid",listFriend.get(position).getUser().getUserId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public listFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new listFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull listFriendViewHolder holder, int position) {
        User friend = listFriend.get(position).getUser();
        holder.txtName.setText(friend.getName());
    }

    @Override
    public int getItemCount() {
        return listFriend.size();
    }
}
