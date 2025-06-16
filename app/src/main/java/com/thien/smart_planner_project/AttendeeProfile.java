package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        TextView name = findViewById(R.id.attendee_username);
        TextView email = findViewById(R.id.attendee_email);

        LinearLayout layout1 = findViewById(R.id.UpcomingEvent);
        Intent intent = getIntent();

        user = (User) intent.getParcelableExtra("user");
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng nhấn vào layout
                Toast.makeText(getApplicationContext(), "Layout clicked!", Toast.LENGTH_SHORT).show();

                // Ví dụ: Chuyển sang activity khác
                Intent intent1 = new Intent(AttendeeProfile.this, EventActivity.class);

                intent1.putExtra("user", user);
                startActivity(intent1);
            }
        });


        LinearLayout layout2 = findViewById(R.id.logOUt);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng nhấn vào layout
                Toast.makeText(getApplicationContext(), "Layout clicked!", Toast.LENGTH_SHORT).show();

                // Ví dụ: Chuyển sang activity khác
                Intent intent2 = new Intent(AttendeeProfile.this, LoginActivity.class);
                startActivity(intent2);
            }
        });

        assert user != null;

        name.setText(user.getName());

        email.setText(user.getEmail());



    }



}
