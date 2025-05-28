package com.thien.smart_planner_project.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thien.smart_planner_project.EventUpdateActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Event;

import java.util.List;

public class OrganizerEventAdapter extends BaseAdapter {

    private List<Event> eventList;
    Context context;
    String role;
    public OrganizerEventAdapter(Context context,List<Event> eventList, String role) {
        this.context = context;
        this.eventList = eventList;
        this.role = role;
    }

    @Override
    public int getCount() {

        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.event_iten_layout, parent, false);
        }
        ImageView image = convertView.findViewById(R.id.image1);
        TextView name = convertView.findViewById(R.id.name1);
        Event item = eventList.get(position);

        if (role.equals("organizer")) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EventUpdateActivity.class);
                    intent.putExtra("event", item);
                    context.startActivity(intent);
                }
            });
        }

        name.setText(item.getName() + "(" + item.getDate() + ")");

        Glide.with(context).load(item.getImageUrl()).into(image);
        return convertView;
    }
}
