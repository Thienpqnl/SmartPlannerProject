package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {


        super.onCreate(bundle);

        setContentView(R.layout.role_layout);


        Intent intent = getIntent();
        String regisEmail = intent.getStringExtra("regisEmail");
        String regisName = intent.getStringExtra("regisName");
        String regisPass = intent.getStringExtra("regisPass");
        String uid = intent.getStringExtra("uid");
        RadioGroup roleGroup = findViewById(R.id.roleGroup);
        Button confirmButton = findViewById(R.id.confirmRoleButton);

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> {
            confirmButton.setEnabled(true);
        });

        confirmButton.setOnClickListener(v -> {
            int selectedId = roleGroup.getCheckedRadioButtonId();
            String role = "";

            if (selectedId == R.id.radioAttendee) {
                role = "attendee";
            } else if (selectedId == R.id.radioOrganizer) {
                role = "organizer";
            }

            // TODO: Goi API de cap nhat role va chuyen sang MainActivity
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
