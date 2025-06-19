package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.ResponseBody;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.NotificationSender;
import com.thien.smart_planner_project.service.SharedPrefManager;
import com.thien.smart_planner_project.service.SocketManager;


import java.util.HashMap;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth ;
    EditText loginPass, loginEmail;
    TextView signUpView;
    Button loginButton;
    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setContentView(R.layout.login_layout);
        final SharedPrefManager instance = SharedPrefManager.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        loginPass = findViewById(R.id.loginPass);
        loginEmail = findViewById(R.id.loginEmail);
        loginButton = findViewById(R.id.loginButton);
        signUpView = findViewById(R.id.signUp);
        SocketManager.getInstance().init("ws://10.0.2.2:3000",LoginActivity.this);
        signUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui long nhap day du email va mat khau", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null && user.isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Dang nhap thanh cong", Toast.LENGTH_SHORT).show();

                                    String uid = user.getUid();

                                    // Goi API truy van role cua user
                                    ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                                    Call<User> call = apiService.getUserById(uid);
                                    call.enqueue(new Callback<User>() {
                                        @Override
                                        public void onResponse(Call<User> call, Response<User> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                String role = response.body().getRole();
                                                User user = response.body();
                                                SocketManager.getInstance().registerUser(user.getUserId());
                                                instance.saveUser(user);
                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful() && task.getResult() != null) {
                                                                String newToken = task.getResult();

                                                                // Gửi lên server: userId + token
                                                                sendTokenToServer(user.getUserId(), newToken);
                                                            }
                                                        });
                                                NotificationSender.sendNotification(
                                                        LoginActivity.this,
                                                        user.getUserId(),
                                                        "Chào mừng bạn!",
                                                        "DA dang nhap.",
                                                        "welcome"
                                                );
                                                if ("organizer".equalsIgnoreCase(role)) {
                                                    Intent intent = new Intent(LoginActivity.this, OrganizerViewActivity.class);
                                                    intent.putExtra("user",user);
                                                    startActivity(intent);
                                                } else {
                                                    Intent intent2 = new Intent(LoginActivity.this, AttendeeProfile.class);
                                                    intent2.putExtra("user", user);
                                                    startActivity(intent2);
                                                    //dùng session
                                                    SessionManager sessionManager=new SessionManager(LoginActivity.this);
                                                    sessionManager.createLoginSession(user.getUserId(), user.getName());
                                                }

                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Khong lay duoc thong tin nguoi dung tu server", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(LoginActivity.this, "Loi ket noi toi server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(LoginActivity.this, "Vui long xac thuc email truoc khi dang nhap", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Exception e = task.getException();
                                Toast.makeText(LoginActivity.this, "Dang nhap that bai: " + (e != null ? e.getMessage() : ""), Toast.LENGTH_LONG).show();
                            }});
            }
        });

    }
}
