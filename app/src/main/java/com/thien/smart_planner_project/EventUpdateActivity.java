package com.thien.smart_planner_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.thien.smart_planner_project.Controller.GMap;
import com.thien.smart_planner_project.callback.UploadCallback;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventUpdateActivity extends AppCompatActivity {
    TextView detailName, detailDate, detailLocal, detailTime, detailSeat, evType, detailDes,listRegisted;
    Button updateEvent, detailJoin, cancelEvent;
    String name, date, local, time, uid, img, seat, des, type,eventId;
    ImageView detailImg;
    String uploadedImageUrl;
    String id;
    double longitude;
    double latitude;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.organizer_event_details);
        detailName = findViewById(R.id.detailName);
        detailDate = findViewById(R.id.detailDate);
        detailLocal = findViewById(R.id.detailLocal);
        detailTime = findViewById(R.id.detailTime);
        detailSeat = findViewById(R.id.detailSeat);
        evType = findViewById(R.id.evType);
        detailDes = findViewById(R.id.detailDes);
        detailImg = findViewById(R.id.detailImg);
        updateEvent = findViewById(R.id.updateEvent);
        detailJoin = findViewById(R.id.detailJoin);
        cancelEvent = findViewById(R.id.cancelEvent);
        listRegisted = findViewById(R.id.listRegistedBtn);

        detailLocal.setOnClickListener(v -> {
            Intent intent1 = new Intent(EventUpdateActivity.this, GMap.class);
            startActivityForResult(intent1, 100);
        });

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            id = event.get_id();
            name = event.getName();
            date = event.getDate();
            local = event.getLocation();
            img = event.getImageUrl();
            time = event.getTime();
            uid = event.getCreatorUid();
            seat = "" + event.getSeats();
            des = event.getDescription();
            type = event.getType();
            longitude = event.getLongitude();
            latitude = event.getLatitude();

        }
        detailName.setText(name);
        detailDate.setText(date);
        detailSeat.setText(seat);
        detailTime.setText(time);
        detailDes.setText(des);
        detailLocal.setText(local);
        Glide.with(this)
                .load(img)
                .into(detailImg);

        setupEditableText(detailName, "Nhap ten moi");
        setupEditableText(detailDate, "Nhap ngay moi");
        setupEditableText(detailTime, "Nhap gio moi");
        setupEditableText(detailSeat, "Nhap cho ngoi moi");
        setupEditableText(evType, "Nhap loai su kien");
         setupEditableText(detailDes, "Nhap mo ta moi");

        detailImg.setOnClickListener(v -> openImagePicker());
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        detailImg.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn
                        detailImg.setTag(selectedImageUri.toString());

                        try {
                            MainActivity main = new MainActivity();
                            main.uploadImage(EventUpdateActivity.this,selectedImageUri, new UploadCallback() {
                                @Override
                                public void onUploadSuccess(String uploadedUrl) {
                                    uploadedImageUrl = uploadedUrl; // Lưu lại để dùng khi tạo Event
                                    Log.d("UPLOAD", "URL: " + uploadedImageUrl);
                                }

                                @Override
                                public void onUploadFailure(Throwable t) {
                                    Toast.makeText(EventUpdateActivity.this, "Lỗi upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        listRegisted.setOnClickListener(v ->{
            if(event != null)
                goListRegisted(event.get_id());
        });

        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView detailName, detailDate, detailLocal, detailTime, detailSeat, evType, detailDes;
                Event event1 = new Event(detailName.getText().toString(), detailDate.getText().toString(),
                        local, detailTime.getText().toString(), evType.getText().toString(),
                        detailDes.getText().toString(), uploadedImageUrl, Integer.parseInt(detailSeat.getText().toString()),
                        longitude, latitude, uid);
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                Call<Event> call = apiService.updateEvent( id, event1);

                call.enqueue(new Callback<Event>() {
                    @Override
                    public void onResponse(Call<Event> call, Response<Event> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EventUpdateActivity.this, "cap nhat thanh cong", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EventUpdateActivity.this, OrganizerViewActivity.class);
                            intent.putExtra("uid", response.body().getCreatorUid());
                            intent.putExtra("role","organizer");
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                                Log.e("API_ERROR", "Code: " + response.code() + " - " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(EventUpdateActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Event> call, Throwable t) {
                        Toast.makeText(EventUpdateActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupEditableText(TextView textView, String hint) {

        textView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cap nhat");

            final EditText input = new EditText(this);
            input.setHint(hint);
            input.setText(textView.getText());
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                textView.setText(input.getText().toString());

            });

            builder.setNegativeButton("Huy", (dialog, which) -> dialog.cancel());

            builder.show();
        });
    }
    private void openImagePicker() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return; // Thoát nếu chưa được cấp quyền
        }

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                local = data.getStringExtra("fullAddress");
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
                detailLocal.setText(local);
            }
        }
    }
    private void goListRegisted(String eventId){
        Intent intent = new Intent(this, AttendeeListActivity.class);
        System.out.println(eventId);
        intent.putExtra("eventId",eventId);
        startActivity(intent);
    }
}
