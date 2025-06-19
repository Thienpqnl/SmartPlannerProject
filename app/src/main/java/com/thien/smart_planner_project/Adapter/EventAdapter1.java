package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter1 extends RecyclerView.Adapter<EventAdapter1.EventViewHolder> {
    private List<Event> originalList;

    public EventAdapter1(List<Event> eventList) {
        this.originalList = eventList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView txtEventName, txtEventLocation, txtEventAmount,txtEventTime;
        ImageView imageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEventName = itemView.findViewById(R.id.attendee_name);
            txtEventLocation = itemView.findViewById(R.id.location);
            txtEventAmount = itemView.findViewById(R.id.amount);
            txtEventTime = itemView.findViewById(R.id.time_attend);
            imageView = itemView.findViewById(R.id.imageView6);
        }
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_layout1, parent, false);
        return new EventViewHolder(view);
    }
    @Override
    public int getItemCount() {
        return originalList.size();
    }
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = originalList.get(position);
        holder.txtEventName.setText(event.getName());
        holder.txtEventLocation.setText(event.getLocation());
        holder.txtEventTime.setText(String.format("Thời gian: %s",event.getDate()));
        holder.txtEventAmount.setText(String.format("Số lượng: %d", event.getSeats()));
        Glide.with(holder.itemView.getContext())
                .load(event.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
    }
}
