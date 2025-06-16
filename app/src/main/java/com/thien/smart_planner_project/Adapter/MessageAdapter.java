package com.thien.smart_planner_project.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Message;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getContent());
        User user = SharedPrefManager.getInstance(holder.itemView.getContext()).getUser();

        // Lấy LayoutParams để chỉnh constraint
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.messageText.getLayoutParams();

        if (message.getSender().equals(user.getUserId())) {
            holder.messageText.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_edittext_receiver_rounded));
            holder.messageText.setTextColor(Color.BLACK);

            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.startToStart = ConstraintLayout.LayoutParams.UNSET;
            holder.messageText.setLayoutParams(params);

            holder.messageText.setGravity(Gravity.END);
        } else {
            holder.messageText.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_edittext_sender_rounded));
            holder.messageText.setTextColor(Color.WHITE);

            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
            holder.messageText.setLayoutParams(params);

            holder.messageText.setGravity(Gravity.START);

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}