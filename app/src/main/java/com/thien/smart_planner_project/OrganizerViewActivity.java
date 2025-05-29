package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thien.smart_planner_project.Adapter.OrganizerEventAdapter;
import com.thien.smart_planner_project.model.CheckinRequest;
import com.thien.smart_planner_project.model.CheckinResponse;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrganizerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle bundle) {


        super.onCreate(bundle);

        setContentView(R.layout.organizer_layout);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        FloatingActionButton fab = findViewById(R.id.fab_add);
        FloatingActionButton fabCheckin = findViewById(R.id.fab_checkin);
        ListView lsView = findViewById(R.id.listViewEvents);
        bottomAppBar.setNavigationOnClickListener(v -> {
            // Mo danh sach
        });
        User user = (User) getIntent().getSerializableExtra("user");
        if(user == null || !"organizer".equals(user.getRole())) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                // Mo trang profile
                return true;
            }
            return false;
        });
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
        });

        fabCheckin.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(OrganizerViewActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan QR code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        });

        Intent intent = getIntent();

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
                Log.e ("err","loi khi goi api: "  + t);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                String qrData = result.getContents();
                sendCheckInRequest(qrData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void sendCheckInRequest(String qrCode) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        CheckinRequest request = new CheckinRequest(qrCode);

        Call<CheckinResponse> call = apiService.checkIn(request);
        call.enqueue(new Callback<CheckinResponse>() {
            @Override
            public void onResponse(Call<CheckinResponse> call, Response<CheckinResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrganizerViewActivity.this,
                            "✅ " + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OrganizerViewActivity.this,
                            "❌ Check-in thất bại",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckinResponse> call, Throwable t) {
                Toast.makeText(OrganizerViewActivity.this,
                        "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
