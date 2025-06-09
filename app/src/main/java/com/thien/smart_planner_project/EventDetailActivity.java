package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.thien.smart_planner_project.Fragment.QRFragment;
import com.thien.smart_planner_project.callback.ApiCallback;
import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private User user, userLogin;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        final SharedPrefManager instance = SharedPrefManager.getInstance(this);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Toolbar toolbar = findViewById(R.id.toolbarEventDetail);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chi tiết sự kiện");
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView txtName = findViewById(R.id.detailName);
        TextView txtTime = findViewById(R.id.detailTime);
        TextView txtLocation = findViewById(R.id.detailLocal);
        TextView txtDate = findViewById(R.id.detailDate);
        TextView txtSeat = findViewById(R.id.detailSeat);
        TextView txtDes = findViewById(R.id.detailDes);
        ImageView imgEvent = findViewById(R.id.detailImg);
        TextView creator = findViewById(R.id.creator);
        Button detailJoin = findViewById(R.id.detailJoin);

        Intent intent = getIntent();
        String id=intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        int seat = intent.getIntExtra("seat", 0);
        String des = intent.getStringExtra("des");
        String date = intent.getStringExtra("date");
        String img = intent.getStringExtra("image");
        String uid = intent.getStringExtra("uid");
        String eventId = intent.getStringExtra("eventId");



        userLogin = instance.getUser();
        if (userLogin == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Toast.makeText(this,"uid"+ uid, Toast.LENGTH_SHORT).show();

        SessionManager sessionManager=new SessionManager(EventDetailActivity.this);
//        Toast.makeText(this,"uid"+ sessionManager.getUserId(), Toast.LENGTH_SHORT).show();

        Intent intent1 = new Intent(EventDetailActivity.this, UserDetailActivity.class);

        txtName.setText("Tên sự kiện: " + name);
        txtTime.setText("Giờ: " +time);
        txtLocation.setText("Vị trí: "+location);
        txtDate.setText("Ngày: "+date);
        txtSeat.setText("Số ghế: "+ seat );
        txtDes.setText(des);

        Glide.with(this)
                .load(img)
                .into(imgEvent);

        if (isPastDate(date)) {
            txtName.setTextColor(Color.GRAY);
            txtName.setText("Tên sự kiện: " +name + " (Đã diễn ra)");
        }
        creator.setOnClickListener(v -> startActivity(intent1));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<User> call = apiService.getUserById(uid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                if (user != null) {
                    intent1.putExtra("name", user.getName());
                    intent1.putExtra("email", user.getEmail());
                    intent1.putExtra("local", user.getLocation());
                    intent1.putExtra("role", user.getRole());
                    intent1.putExtra("uid", uid);
                    creator.setText(user.getName());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("err", "Lỗi khi gọi api: " + t);
            }
        });

        // Đặt vé
        detailJoin.setOnClickListener(v -> {
            //giả sử lấy name sự kiện
            Booking bookingRequest = new Booking(eventId, uid,sessionManager.getUserId());
            apiService.createBooking(bookingRequest).enqueue(new ApiCallback<Booking>() {
                @Override
                public void onSuccess(Booking result) {
                    String qrUrl = result.getUrlQR();
                    QRFragment qrFragment = QRFragment.newInstance(qrUrl);
                    qrFragment.show(getSupportFragmentManager(), "QRFragment");
                }
                @Override
                public void onError(String errorMessage) {
                    // Kiểm tra thông báo lỗi
                    if (errorMessage.contains("Số lượng booking đã đạt giới hạn seats")) {
                        // Hiển thị AlertDialog nếu vượt quá seats
                        new AlertDialog.Builder(EventDetailActivity.this)
                                .setTitle("Thông báo")
                                .setMessage("Số lượng booking đã đạt giới hạn. Vui lòng thử lại sau.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        // Các lỗi khác
                        Toast.makeText(EventDetailActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show(); // Dùng EventDetailActivity.this thay vì context
                    }
                }

            });
        });
    }

    public void downloadQRImage(String qrUrl) {
        Glide.with(this)
                .asBitmap()
                .load(qrUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveImageToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toast.makeText(EventDetailActivity.this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QR_" + System.currentTimeMillis() + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyApp");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri == null) throw new IOException("Không thể tạo Uri");
                fos = resolver.openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/MyApp";
                File file = new File(imagesDir);
                if (!file.exists()) file.mkdirs();
                String fileName = "QR_" + System.currentTimeMillis() + ".png";
                File image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(image));
                sendBroadcast(intent);
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (fos != null) fos.close();

            Toast.makeText(this, "Đã tải ảnh vào thư mục Pictures/MyApp", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isPastDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        try {
            Date inputDate = sdf.parse(dateStr);
            Date today = new Date();
            return inputDate.before(today);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
