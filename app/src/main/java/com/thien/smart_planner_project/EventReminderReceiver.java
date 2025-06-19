package com.thien.smart_planner_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

public class EventReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "event_reminder_channel";
    private static final String CHANNEL_NAME = "Nhắc nhở sự kiện";

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventName = intent.getStringExtra("event_name");
        String eventId = intent.getStringExtra("event_id");
        String reminderType = intent.getStringExtra("reminder_type");

        String title = "Nhắc nhở sự kiện";
        String message = "";

        if ("reminder_1day".equals(reminderType)) {
            message = "Sự kiện '" + eventName + "' sẽ diễn ra vào ngày mai. Đừng quên nhé!";
        } else if ("reminder_ontime".equals(reminderType)) {
            message = "Sự kiện '" + eventName + "' đang diễn ra. Hãy tham gia ngay!";
        } else {
            message = "Sự kiện '" + eventName + "' sắp diễn ra!";
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent activityIntent = new Intent(context, EventDetailActivity.class);
        activityIntent.putExtra("eventId", eventId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int notificationId = ("reminder_1day".equals(reminderType)) ? eventId.hashCode() + 1 : eventId.hashCode();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false) // Không tự tắt khi bấm vào
                .setOngoing(true)     // Không cho vuốt bỏ dễ dàng
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Gửi notification
        notificationManager.notify(notificationId, builder.build());

        // Hẹn 1 phút sau tự động hủy notification
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            notificationManager.cancel(notificationId);
        }, 60 * 1000);
    }
}