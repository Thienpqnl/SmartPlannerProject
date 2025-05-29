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
            integrator.setPrompt("Quét mã QR vé tham dự");
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setCameraId(0);
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
        if (result != null && result.getContents() != null) {
            String qrCode = result.getContents();
            sendQRCodeToServer(qrCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendQRCodeToServer(String qrCode) {
        String url = "http://<your-server-ip>:<port>/api/checkin";
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("qrCode", qrCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> Toast.makeText(this, "Check-in thành công!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Check-in thất bại!", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}
