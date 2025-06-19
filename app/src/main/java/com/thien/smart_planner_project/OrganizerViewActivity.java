package com.thien.smart_planner_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.thien.smart_planner_project.Adapter.OrganizerEventAdapter;
import com.thien.smart_planner_project.callback.ApiCallback;
import com.thien.smart_planner_project.model.CheckinRequest;
import com.thien.smart_planner_project.model.CheckinResponse;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;
import com.thien.smart_planner_project.service.SocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerViewActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 101;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.organizer_layout);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fab = findViewById(R.id.fab_add);
        FloatingActionButton fabCheckin = findViewById(R.id.fab_checkin);
        ListView lsView = findViewById(R.id.listViewEvents);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        if (user == null || !"organizer".equals(user.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        // Xử lý khi bấm nút navigation (nút 3 gạch bên trái)
        bottomAppBar.setNavigationOnClickListener(v -> {
            // Hiện PopupMenu khi bấm navigation icon
            PopupMenu popupMenu = new PopupMenu(OrganizerViewActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.bottom_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_profile) {
                    // Mở trang cá nhân
                    Intent intent = new Intent(OrganizerViewActivity.this, UserDetailActivity.class);
                    startActivity(intent);
                    return true;
                }
                // Thêm các item khác nếu cần
                else if (item.getItemId() == R.id.menu_logout) {
                    // Xử lý đăng xuất
                    handleLogout();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        // Xử lý các item của menu trên BottomAppBar (nếu có)
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(OrganizerViewActivity.this, UserDetailActivity.class);
                startActivity(intent);
                return true;
            }
            // Thêm xử lý các item khác ở đây
            return false;
        });
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        fabCheckin.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            } else {
                launchQRScanner();
            }
        });


        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getOrganizerEventList(user.getUserId());

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                assert response.body() != null;
                List<Event> eventList = new ArrayList<>(response.body());
                Log.e("log-loi", eventList.size() + "");
                OrganizerEventAdapter adapter = new OrganizerEventAdapter(OrganizerViewActivity.this, eventList, user.getRole());
                lsView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("err", "loi khi goi api: " + t);
            }
        });
        catchNotification();
    }

    private void handleLogout() {
        // Xóa dữ liệu người dùng đã lưu (ví dụ SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // hoặc editor.remove("user_id");
        editor.apply();

        // Quay lại màn hình đăng nhập, xóa toàn bộ backstack
        Intent intent = new Intent(OrganizerViewActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private final ActivityResultLauncher<ScanOptions> qrScanner = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String qrContent = result.getContents();
                    if (qrContent.contains("_")) {
                        String[] parts = qrContent.split("_");
                        String userId = parts[0];
                        String eventId = parts[1];
                        checkInAttendee(userId, eventId);
                    } else {
                        Toast.makeText(this, "QR không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Quét bị hủy", Toast.LENGTH_SHORT).show();
                }
            }
    );
    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Đưa mã QR vào khung hình");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        qrScanner.launch(options);
    }
    private void checkInAttendee(String userId, String eventId) {
        CheckinRequest request = new CheckinRequest(userId, eventId);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.checkIn(request).enqueue(new ApiCallback<CheckinResponse>() {
            @Override
            public void onSuccess(CheckinResponse result) {
                String message = result.getMessage();
                boolean success = result.isSuccess();
                Toast.makeText(OrganizerViewActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void catchNotification(){
        Socket socket = SocketManager.getInstance().getSocket();
        if (!socket.connected()) socket.connect();

        // Lắng nghe thông báo toàn app
        socket.on("receive notification", args -> runOnUiThread(() -> {
            try {
                JSONObject data = (JSONObject) args[0];
                String type = data.getString("type");
                String content = data.getString("content");

                // Tùy ý: hiện Toast, badge, Notification...
                Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

                // Gợi ý: chuyển tiếp đến ViewModel / shared data nếu cần

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
