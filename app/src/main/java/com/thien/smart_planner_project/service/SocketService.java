package com.thien.smart_planner_project.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.thien.smart_planner_project.R;

public class SocketService extends Service {
    private static final String CHANNEL_ID = "socket_service_channel";
    private static final String CHANNEL_NAME = "Socket Service";
    private NotificationHelper notificationHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        notificationHelper = new NotificationHelper(this);
        // Thay đổi dòng này để truyền context
        SocketManager.getInstance().init("ws://10.0.2.2:3000", this);
        SocketManager.getInstance().connect();
        // Đăng ký lắng nghe notification
        SocketManager.getInstance().setUpListeners(notificationHelper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, buildNotification());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        SocketManager.getInstance().disconnect();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(chan);
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("App đang kết nối")
                .setContentText("Socket.io đang hoạt động")
                .setSmallIcon(R.drawable.ic_notification)
                .setOngoing(true)
                .build();
    }
}