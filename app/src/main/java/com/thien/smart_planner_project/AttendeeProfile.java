package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thien.smart_planner_project.model.User;

public class AttendeeProfile extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.attendee_main_layout);
        TextView name = findViewById(R.id.attendee_username);
        TextView email = findViewById(R.id.attendee_email);

        LinearLayout layout1 = findViewById(R.id.UpcomingEvent);
        LinearLayout logOutLayout = findViewById(R.id.logOUt);



        Intent intent = getIntent();

        User user = (User) getIntent().getSerializableExtra("user");
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng nhấn vào layout
                Toast.makeText(AttendeeProfile.this, "Layout clicked!", Toast.LENGTH_SHORT).show();

                // Ví dụ: Chuyển sang activity khác
                Intent intent1 = new Intent(AttendeeProfile.this, EventActivity.class);

                intent1.putExtra("user", user);
                startActivity(intent1);
            }
        });


        LinearLayout layout2 = findViewById(R.id.logOUt);

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng nhấn vào layout
                Toast.makeText(AttendeeProfile.this, "Layout clicked!", Toast.LENGTH_SHORT).show();

                // Ví dụ: Chuyển sang activity khác
                Intent intent2 = new Intent(AttendeeProfile.this, LoginActivity.class);
                startActivity(intent2);
            }
        });
        LinearLayout layoutFriends = findViewById(R.id.individual_friend);
        layoutFriends.setOnClickListener(v -> {
            Intent intent1 = new Intent(AttendeeProfile.this, MainChatActivity.class);
            startActivity(intent1);
        });
        assert user != null;

        name.setText(user.getName());

        email.setText(user.getEmail());



        logOutLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(AttendeeProfile.this, "Logged out", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(AttendeeProfile.this, LoginActivity.class));
            }
        });


        ImageView btnAttNotification = findViewById(R.id.btnAttNotification);

        btnAttNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AttendeeProfile.this, AttendeeNotificationActivity.class);
                intent1.putExtra("userId", user.getUserId());
                startActivity(intent1);
            }
        });

    }


}
