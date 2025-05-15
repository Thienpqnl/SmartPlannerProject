package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thien.smart_planner_project.Controller.GMap;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSetProfileActivity extends AppCompatActivity {
    TextView nameView,emailView ,roleView;
    Button edtBtn;
    private double longitude;
    private  double latitude;
    private ImageView btnPickLocation;
    private String fullAddress;
    private AutoCompleteTextView edtAddress;

    String role, regisEmail, regisName, regisPass, uid;
    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);

        setContentView(R.layout.user_set_layout);

         nameView = findViewById(R.id.textNameValue);
         emailView = findViewById(R.id.textEmailValue);
         roleView = findViewById(R.id.textRoleValue);
        edtBtn = findViewById(R.id.buttonEdit);
        edtAddress=findViewById(R.id.edtAddress);
        btnPickLocation=findViewById(R.id.btnPickLocation);
        Intent intent = getIntent();

        role = intent.getStringExtra("role");
        regisEmail = intent.getStringExtra("email");
        regisName = intent.getStringExtra("name");
        regisPass = intent.getStringExtra("regisPass");
        uid = intent.getStringExtra("uid");

        nameView.setText(regisName);
        emailView.setText(regisEmail);
        roleView.setText(role);

        btnPickLocation.setOnClickListener(v -> {
            Intent intent1 = new Intent(UserSetProfileActivity.this, GMap.class);
            startActivityForResult(intent1, 100);
        });

        edtBtn.setOnClickListener(v -> saveUser());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                fullAddress = data.getStringExtra("fullAddress");
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);

                edtAddress.setText(fullAddress);

            }
        }
    }

    private void saveUser() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        //String email,  String name, String location ,String role, long longitude, long latitude
        Call<User> call = apiService.createUser(new User(uid, regisEmail,regisName, fullAddress, role, longitude, latitude));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserSetProfileActivity.this, "Tạo sự kiện thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserSetProfileActivity.this, EventActivity.class));
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("API_ERROR", "Code: " + response.code() + " - " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(UserSetProfileActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserSetProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
