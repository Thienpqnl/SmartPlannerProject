package com.thien.smart_planner_project.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.UserDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.UserPreviewActivity;
import com.thien.smart_planner_project.model.User;

import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {
    private static List<User> users;

    public AttendeeAdapter(List<User> users) {
        this.users = users;
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder{
        TextView attendeeName;
        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            attendeeName = itemView.findViewById(R.id.attendee_name);
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                User user = users.get(position);
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(v.getContext(), UserPreviewActivity.class);
                    intent.putExtra("uid",user.getUserId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    @NonNull
    @Override
    public AttendeeAdapter.AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
       User user = users.get(position);
       holder.attendeeName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
