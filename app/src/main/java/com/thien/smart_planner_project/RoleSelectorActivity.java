package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class RoleSelectorActivity extends AppCompatActivity {
    String role = "";
    @Override
    protected void onCreate(Bundle bundle) {


        super.onCreate(bundle);

        setContentView(R.layout.role_layout);


        Intent intent = getIntent();
        String regisEmail = intent.getStringExtra("regisEmail");
        String regisName = intent.getStringExtra("regisName");
        String regisPass = intent.getStringExtra("regisPass");
        String uid = intent.getStringExtra("uid");

        MaterialCardView cardTochuc = findViewById(R.id.cardViewOrganizer);

        MaterialCardView cardThamGia = findViewById(R.id.cardViewAttendee);

        cardTochuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = "organizer";
            }
        });

        cardThamGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role = "attendee";
            }
        });

        Button confirmButton = findViewById(R.id.confirmRoleButton);


        confirmButton.setOnClickListener(v -> {


            Toast.makeText(this, "Role da chon: " + role, Toast.LENGTH_SHORT).show();

            Intent intent1 = new Intent(RoleSelectorActivity.this, UserSetProfileActivity.class);

            intent1.putExtra("role", role);
            intent1.putExtra("name", regisName);
            intent1.putExtra("email", regisEmail);
            intent1.putExtra("regisPass", regisPass);
            intent1.putExtra("uid", uid);
            startActivity(intent1);
        });
    }
}
