package com.thien.smart_planner_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresExtension;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.thien.smart_planner_project.model.Event;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    private EditText edtDate;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private AutoCompleteTextView edtAddress;
    private ImageView btnPickLocation;
    private PlacesClient placesClient;
    private  EditText edtSeat;
    private  EditText edtName;
    private EditText edtDes;
    private Button creButton;
    EditText edtTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);
        edtName = findViewById(R.id.textName);
        imageView = findViewById(R.id.imageView);
        edtDate = findViewById(R.id.edtDate);
        edtSeat = findViewById(R.id.editSeat);

        edtDate.setOnClickListener(v -> showDatePicker());
        edtDes = findViewById(R.id.edtDescription);
        edtTime = findViewById(R.id.edtTime);

        edtTime.setOnClickListener(v -> showTimePicker());
        creButton = findViewById(R.id.button);
        // Khởi tạo Photo Picker API
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
        );
        // Khi người dùng nhấn vào ảnh
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
            imageView.setOnClickListener(v -> openImagePicker());
        }

        creButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);

                // Lấy URI của hình ảnh từ ImageView
                Uri imageUri = Uri.parse(imageView.getTag().toString());

                // Gửi URI qua Intent

                Event event = new Event(edtName.getText().toString(),
                       edtDate.getText().toString(),
                        edtTime.getText().toString(),
                        "Hà Nội",
                        edtSeat.getText().toString(),
                         imageUri.toString(), edtDes.getText().toString());


                intent.putExtra("event_data", event);
                startActivity(intent);

            }
        });
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private void openImagePicker() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
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
