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

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.ResponseBody;
import com.thien.smart_planner_project.Controller.GMap;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.NotificationSender;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        User user = new User(uid, regisEmail,regisName, fullAddress, role, longitude, latitude);
        Call<User> call = apiService.createUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(UserSetProfileActivity.this, "Tạo nguươời dung thành conng", Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    String newToken = task.getResult();
                                    // Gửi lên server: userId + token
                                    sendTokenToServer(user.getUserId(), newToken);
                                }
                            });
                    if (role.equals("attendee")) {
                        Intent newIntent = new Intent(UserSetProfileActivity.this, AttendeeProfile.class);
                        newIntent.putExtra("user", user);
                        NotificationSender.sendNotification(
                                UserSetProfileActivity.this,
                                user.getUserId(),
                                "Chào mừng bạn!",
                                "Cảm ơn bạn đã đăng ký ứng dụng của chúng tôi.",
                                "welcome"
                        );
                        startActivity(newIntent);
                    }
                    else {
                        startActivity(new Intent(UserSetProfileActivity.this, OrganizerViewActivity.class));
                    }
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

            private void sendTokenToServer(String userId, String newToken) {
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Map<String, String> payload = new HashMap<>();
                payload.put("userId", userId);
                payload.put("fcmToken", newToken);

                Call<ResponseBody> call = apiService.saveToken(payload);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("FCM", "Token đã được cập nhật");
                        } else {
                            Log.e("FCM", "Lỗi khi cập nhật token: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("FCM", "Lỗi mạng khi gửi token", t);
                    }
                });
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserSetProfileActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
