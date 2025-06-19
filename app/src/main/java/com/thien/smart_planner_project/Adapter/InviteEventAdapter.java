package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteEventAdapter extends RecyclerView.Adapter<InviteEventAdapter.EventViewHolder> {
    private List<Event> originalList;
    private Map<Integer, Boolean> choosePosition;
    public InviteEventAdapter(List<Event> eventList) {
        this.originalList = eventList;
        choosePosition = new HashMap<>();
        for (int i = 0; i < originalList.size(); i++) {
            choosePosition.put(i,false); // khởi tạo mặc định
        }
    }
    public interface OnCheckedCountChangeListener {
        void onCheckedCountChanged(int count);
    }

    private OnCheckedCountChangeListener listener;

    public void setOnCheckedCountChangeListener(OnCheckedCountChangeListener listener) {
        this.listener = listener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView txtEventName, txtEventLocation, txtEventAmount,txtEventTime;
        ImageView imageView;
        CheckBox checkBox;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEventName = itemView.findViewById(R.id.attendee_name);
            txtEventLocation = itemView.findViewById(R.id.location);
            txtEventAmount = itemView.findViewById(R.id.amount);
            txtEventTime = itemView.findViewById(R.id.time_attend);
            imageView = itemView.findViewById(R.id.imageView6);
            checkBox = itemView.findViewById(R.id.checkbox_select);
        }
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_event_item, parent, false);
        return new EventViewHolder(view);
    }
    @Override
    public int getItemCount() {
        return originalList.size();
    }
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Event event = originalList.get(position);
        holder.txtEventName.setText(event.getName());
        holder.txtEventLocation.setText(event.getLocation());
        holder.txtEventTime.setText(String.format("Thời gian: %s",event.getDate()));
        Glide.with(holder.itemView.getContext())
                .load(event.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);
        System.out.println(event);
        holder.checkBox.setChecked(Boolean.TRUE.equals(choosePosition.get(position)));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                choosePosition.put(position, isChecked);
                if(listener != null) {
                    int count = 0;
                    for(Boolean b : choosePosition.values()) {
                        if(Boolean.TRUE.equals(b)) count++;
                    }
                    listener.onCheckedCountChanged(count);
                }
            }
        });
    }
    public Map<Integer, Boolean> getChoosePosition(){
        return choosePosition;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void selectAll(boolean checked) {
        for (int i = 0; i < originalList.size(); i++) {
            choosePosition.put(i,checked);
        }
        notifyDataSetChanged();
    }
    public int getSelectedCount(){
        return (int) choosePosition.keySet().stream().filter(choosePosition::get).count();
    }

}
