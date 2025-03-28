package com.thien.smart_planner_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thien.smart_planner_project.Controller.FirestoreHelper;
import com.thien.smart_planner_project.Controller.GMap;
import com.thien.smart_planner_project.model.Event;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    private EditText edtDate,edtSeat,edtName,edtDes,edtTime;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private AutoCompleteTextView edtAddress;
    private ImageView btnPickLocation;
    private PlacesClient placesClient;
    private Button creButton;
    private FirestoreHelper firestoreHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        firestoreHelper = new FirestoreHelper();
        edtName = findViewById(R.id.textName);
        imageView = findViewById(R.id.imageView);
        edtDate = findViewById(R.id.edtDate);
        edtSeat = findViewById(R.id.editSeat);
        edtAddress=findViewById(R.id.edtAddress);

        edtDate.setOnClickListener(v -> showDatePicker());
        edtDes = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);
        btnPickLocation=findViewById(R.id.btnPickLocation);
        edtTime.setOnClickListener(v -> showTimePicker());

        creButton = findViewById(R.id.button);
        // Khởi tạo Photo Picker API
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri);
                        imageView.setTag(selectedImageUri.toString());
                    }
                }
        );

        imageView.setOnClickListener(v -> openImagePicker());

        creButton.setOnClickListener(v -> saveEvent());

        btnPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GMap.class);
            intent.putExtra("name", edtName.getText().toString());
            intent.putExtra("date", edtDate.getText().toString());
            intent.putExtra("time", edtTime.getText().toString());
            intent.putExtra("description", edtDes.getText().toString());
            intent.putExtra("seats", edtSeat.getText().toString());  // Lưu ý: Truyền chuỗi thay vì số nguyên
            intent.putExtra("imageURL", imageView.getTag() != null ? imageView.getTag().toString() : "");
            startActivityForResult(intent, 100);
        });


    }
    //set address
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null) {
                String fullAddress = data.getStringExtra("fullAddress");
                edtAddress.setText(fullAddress);  // Gán lại địa chỉ vào EditText
            }
        }
    }

    private void saveEvent() {
        String name = edtName.getText().toString();
        String date = edtDate.getText().toString();
        String time = edtTime.getText().toString();
        String location = edtAddress.getText().toString();
        String description = edtDes.getText().toString();
        int seats = Integer.parseInt(edtSeat.getText().toString());
        String imageURL = imageView.getTag().toString();

        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty() || imageURL.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreHelper.saveEvent(imageURL, name, date, time, location, seats, description, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(String eventId) {
                Toast.makeText(MainActivity.this, "Sự kiện đã được tạo!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
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


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng ngày thành dd/MM/yyyy
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    edtDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
    private void showTimePicker() {
        // Lấy thời gian hiện tại
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Tạo TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                (view, hourOfDay, minute1) -> {
                    // Hiển thị giờ đã chọn lên EditText
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    edtTime.setText(time);
                },
                hour, minute, false // `true` để hiển thị 24h, `false` nếu muốn 12h AM/PM
        );
        timePickerDialog.show();
    }
}