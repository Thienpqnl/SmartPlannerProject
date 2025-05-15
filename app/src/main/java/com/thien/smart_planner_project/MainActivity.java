package com.thien.smart_planner_project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.Timestamp;
import com.thien.smart_planner_project.Controller.FirestoreHelper;
import com.thien.smart_planner_project.Controller.GMap;
import com.thien.smart_planner_project.callback.UploadCallback;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.ImageUploadResponse;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.network.UploadAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText edtDate,edtSeat,edtName,edtDes,edtTime;
    private com.google.firebase.Timestamp timestamp;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private AutoCompleteTextView edtAddress;
    private ImageView btnPickLocation;
    private PlacesClient placesClient;
    private Button creButton;
    private FirestoreHelper firestoreHelper;
    private double longitude;
    private  double latitude;
    String selectedItem;
    String uploadedImageUrl;
    private  String[] categories = {" Sự kiện doanh nghiệp", " Sự kiện xã hội", "Sự kiện từ thiện",
            "Sự kiện thể thao & giải trí", "Sự kiện ăn uống đặc biệt"};
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

        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        // Set simple layout resource file for each item of spinner
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the Spinner which binds data to spinner
        spin.setAdapter(ad);

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
                        imageView.setImageURI(selectedImageUri); // Hiển thị ảnh đã chọn
                        imageView.setTag(selectedImageUri.toString());

                        // ✅ Gọi upload ngay sau khi chọn ảnh
                        try {
                            uploadImage(MainActivity.this,selectedImageUri, new UploadCallback() {
                                @Override
                                public void onUploadSuccess(String uploadedUrl) {
                                    uploadedImageUrl = uploadedUrl; // Lưu lại để dùng khi tạo Event
                                    Log.d("UPLOAD", "URL: " + uploadedImageUrl);
                                }

                                @Override
                                public void onUploadFailure(Throwable t) {
                                    Toast.makeText(MainActivity.this, "Lỗi upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
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
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);

                edtAddress.setText(fullAddress);

                // Lưu tọa độ vào biến để dùng khi lưu sự kiện
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }
    public File getFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
        File tempFile = new File(context.getCacheDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }
    public void uploadImage(Context context,Uri imageUri, UploadCallback callback) throws IOException {
        File file = getFileFromUri(context, imageUri);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .client(client) // 👈 Thêm dòng này
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UploadAPI service = retrofit.create(UploadAPI.class);

        Call<ImageUploadResponse> call = service.uploadImage(body);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().getImageUrl(); // Cập nhật theo model của bạn
                    callback.onUploadSuccess(imageUrl);
                } else {
                    callback.onUploadFailure(new Exception("Upload failed: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                Log.e("UPLOAD", "Upload failed: " + t.getMessage());
                if (t.getCause() != null) {
                    Log.e("UPLOAD", "Cause: " + t.getCause().toString());
                }
                t.printStackTrace();
                callback.onUploadFailure(t);
            }
        });
    }

    private void saveEvent() {

        String editSeat = edtSeat.getText().toString();
        if (editSeat.isEmpty()) {
            Toast.makeText(MainActivity.this, "Vui lòng nhập số ghế!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = edtName.getText().toString();
        String date = edtDate.getText().toString();
        String time = edtTime.getText().toString();
        String location = edtAddress.getText().toString();
        String description = edtDes.getText().toString();

        int seats;
        try {
            seats = Integer.parseInt(editSeat);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Số ghế không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra giá trị số ghế
        if (seats <= 1) {
            Toast.makeText(MainActivity.this, "Số ghế không được dưới 2", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(time) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(description) ||
                uploadedImageUrl == null || uploadedImageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Event> call = apiService.createEvent(new Event("Event: " + System.currentTimeMillis(),name, date, location,  time, selectedItem,description, uploadedImageUrl, seats, longitude, latitude));
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(MainActivity.this, "Tạo sự kiện thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, EventActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    public Timestamp convertToTimestamp(int day, int month, int year) {
        // Tạo đối tượng Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Tháng trong Calendar bắt đầu từ 0
        calendar.set(Calendar.DAY_OF_MONTH, day);

        // Lấy đối tượng Date từ Calendar
        Date date = calendar.getTime();

        // Chuyển thành Timestamp
        return new Timestamp(date);
    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate now = LocalDate.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);

                    if (!selectedDate.isAfter(now)) {
                        Toast.makeText(MainActivity.this, "Su kien phai duoc tao truoc it nhat 1 ngay", Toast.LENGTH_SHORT).show();
                    } else {
                        // Định dạng ngày thành dd/MM/yyyy
                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtDate.setText(formattedDate);

                        // Chuyển đổi thành timestamp (nếu cần)
                        timestamp = convertToTimestamp(selectedDay, selectedMonth + 1, selectedYear);
                    }
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Make toast of the name of the course which is selected in the spinner
        Toast.makeText(getApplicationContext(), categories[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed when no selection is made
    }
}
