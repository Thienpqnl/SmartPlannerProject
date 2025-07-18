package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import com.thien.smart_planner_project.service.NotificationSender;
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
                    NotificationSender.sendNotification(
                            EventDetailActivity.this,
                            user.getUserId(),
                            "SmartPlannerProject",
                            "Dat ve thanh cong.",
                            "welcome"
                    );
                    scheduleEventReminder(eventId, name, date, time, -1 * 24 * 60 * 60 * 1000, eventId.hashCode() + 2, "reminder_1day");
                    // Đặt nhắc nhở trước 1 ngày
                    scheduleEventReminder(eventId, name, date, time, -3 * 24 * 60 * 60 * 1000, eventId.hashCode() + 1, "reminder_day");
                    // Đặt nhắc nhở đúng giờ sự kiện
                    scheduleEventReminder(eventId, name, date, time, 0, eventId.hashCode(), "reminder_ontime");

                    Toast.makeText(EventDetailActivity.this, "Đã đặt vé và nhắc nhở sự kiện!", Toast.LENGTH_SHORT).show();

                    String qrUrl = result.getUrlQR();
                    QRFragment qrFragment = QRFragment.newInstance(qrUrl);
                    qrFragment.setOnQRDialogDismissListener(new QRFragment.OnQRDialogDismissListener() {
                        @Override
                        public void onQRDialogDismiss() {
                            finish();
                        }
                    });
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



    private void scheduleEventReminder(String eventId, String eventName, String eventDate, String eventTime) {
        // eventDate: "dd/M/yyyy", eventTime: "HH:mm" (ví dụ "20/06/2025 14:00")
        String dateTimeStr = eventDate + " " + eventTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault());
        try {
            Date eventDateTime = sdf.parse(dateTimeStr);
            if (eventDateTime == null) return;

            // Thời điểm gửi thông báo nhắc (ví dụ: trước 30 phút)
            long notifyTime = eventDateTime.getTime() - 30 * 60 * 1000;
            if (notifyTime < System.currentTimeMillis()) return; // Đã quá giờ thì bỏ qua

            Intent intent = new Intent(this, EventReminderReceiver.class);
            intent.putExtra("event_name", eventName);
            intent.putExtra("event_id", eventId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    eventId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Kiểm tra quyền SCHEDULE_EXACT_ALARM trên Android 12+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Bạn cần cấp quyền báo thức chính xác (Exact Alarm) trong Cài đặt ứng dụng để nhận nhắc nhở.", Toast.LENGTH_LONG).show();
                    // Gợi ý mở màn hình cấp quyền
                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(settingsIntent);
                    return;
                }
            }

            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notifyTime,
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            notifyTime,
                            pendingIntent
                    );
                }
                Toast.makeText(this, "Đã đặt nhắc nhở cho sự kiện!", Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Không thể đặt nhắc nhở do thiếu quyền Exact Alarm!", Toast.LENGTH_LONG).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Đặt báo thức nhắc nhở sự kiện.
     * @param eventId Mã sự kiện
     * @param eventName Tên sự kiện
     * @param eventDate Ngày diễn ra "dd/M/yyyy"
     * @param eventTime Giờ diễn ra "HH:mm"
     * @param offsetMillis Khoảng thời gian báo trước (VD: -1 ngày = -86400000 ms, đúng giờ = 0)
     * @param requestCode Mã PendingIntent (mỗi loại nhắc nhở 1 mã riêng)
     * @param reminderType String để phân biệt loại nhắc nhở
     */
    private void scheduleEventReminder(String eventId, String eventName, String eventDate, String eventTime,
                                       long offsetMillis, int requestCode, String reminderType) {
        String dateTimeStr = eventDate + " " + eventTime;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy HH:mm", Locale.getDefault());
        try {
            Date eventDateTime = sdf.parse(dateTimeStr);
            if (eventDateTime == null) return;

            long notifyTime = eventDateTime.getTime() + offsetMillis;
            if (notifyTime < System.currentTimeMillis()) return; // Nếu thời điểm đã qua thì thôi

            Intent intent = new Intent(this, EventReminderReceiver.class);
            intent.putExtra("event_name", eventName);
            intent.putExtra("event_id", eventId);
            intent.putExtra("reminder_type", reminderType);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(settingsIntent);
                    return;
                }
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            notifyTime,
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            notifyTime,
                            pendingIntent
                    );
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
