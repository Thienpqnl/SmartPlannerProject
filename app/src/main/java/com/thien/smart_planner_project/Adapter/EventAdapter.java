package com.thien.smart_planner_project.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mongodb.lang.NonNull;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtTime, txtLocation;
        ImageView imgEvent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            imgEvent = itemView.findViewById(R.id.imgEvent);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.txtName.setText(event.getName());
        holder.txtTime.setText(event.getDate() + " " + event.getTime());
        holder.txtLocation.setText(event.getLocation());
        String imageUrl = event.getImageUrl();
        Log.d("EventAdapter", "Image URL: " + imageUrl); // In ra logcat

        Glide.with(holder.itemView.getContext())

                .load(event.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imgEvent);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
