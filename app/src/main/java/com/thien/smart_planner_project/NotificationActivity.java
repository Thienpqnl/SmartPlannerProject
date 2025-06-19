package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Lấy dữ liệu từ Intent (nếu có)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("notification_data")) {
            String data = intent.getStringExtra("notification_data");
            TextView textView = findViewById(R.id.textViewNotification);
            textView.setText(data);
        }
    }
}