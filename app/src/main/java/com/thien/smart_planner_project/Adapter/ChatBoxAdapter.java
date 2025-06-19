package com.thien.smart_planner_project.Adapter;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.ChatBoxDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.dto.ChatBoxDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ChatBoxAdapter extends RecyclerView.Adapter<ChatBoxAdapter.ChatViewHolder> {
    private static List<ChatBoxDTO> chatBoxDTOS;
    public ChatBoxAdapter(List<ChatBoxDTO> chatBoxDTOS) {
        this.chatBoxDTOS = chatBoxDTOS;

    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, time;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_chat_item);
            time = itemView.findViewById(R.id.time_chat_item);
            content = itemView.findViewById(R.id.last_inbox_chat_item);
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(v.getContext(), ChatBoxDetailActivity.class);
                    intent.putExtra("friendId",chatBoxDTOS.get(position).getFriendId());
                    intent.putExtra("uid",chatBoxDTOS.get(position).getUser().getUserId());
                    intent.putExtra("name",chatBoxDTOS.get(position).getUser().getName());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    @NonNull
    @Override
    public ChatBoxAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_box_item, parent, false);
        return new ChatBoxAdapter.ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxAdapter.ChatViewHolder holder, int position) {
        ChatBoxDTO chatBoxDTO = chatBoxDTOS.get(position);

        holder.name.setText(chatBoxDTO.getUser().getName());
        holder.content.setText(chatBoxDTO.getLastMessage().getContent());
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(chatBoxDTO.getLastMessage().getCreatedAtDateTime(), now);

        String timeAgo;
        if (seconds < 60) {
            timeAgo = seconds + " giây trước";
        } else if (seconds < 3600) {
            timeAgo = (seconds/60) + " phút trước";
        } else if (seconds < 86400) {
            timeAgo = (seconds/3600) + " giờ trước";
        } else {
            timeAgo = (seconds/86400) + " ngày trước";
        }
        holder.time.setText(timeAgo);

        User user = SharedPrefManager.getInstance(holder.itemView.getContext()).getUser();
        if(!user.getUserId().equals(chatBoxDTO.getLastMessage().getSender()) && !chatBoxDTO.getLastMessage().isRead()){
            holder.content.setTypeface(null, Typeface.BOLD);
        }else {
            holder.content.setTypeface(null, Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return chatBoxDTOS.size();
    }
}
