package com.thien.smart_planner_project.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thien.smart_planner_project.model.User;

public class AttendeeAdapter extends ArrayAdapter<User> {
    public AttendeeAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
