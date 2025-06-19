package com.thien.smart_planner_project.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {
    private static SocketManager instance;
    private Socket socket;
    private Context appContext;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) instance = new SocketManager();
        return instance;
    }

    public void init(String url, Context context) {
        try {
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay = 1000;
            opts.reconnectionDelayMax = 5000;
            opts.timeout = 20000;
            socket = IO.socket(url, opts);
            this.appContext = context.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (socket != null && !socket.connected()) socket.connect();
    }

    public void disconnect() {
        if (socket != null) socket.disconnect();
    }

    public Socket getSocket() {
        return socket;
    }

    public void registerUser(String userId) {
        if (socket != null) {
            socket.off(Socket.EVENT_CONNECT); // Xóa listener cũ nếu có
            socket.on(Socket.EVENT_CONNECT, args -> socket.emit("register", userId));
            if (!socket.connected()) socket.connect();
            else socket.emit("register", userId); // nếu đã connect thì emit luôn
        }
    }

    // Thêm hàm này để thiết lập listener nhận chat và notification
    public void setUpListeners(final NotificationHelper notificationHelper) {
        if (socket == null) return;

        // Nhận tin nhắn chat
        socket.on("chat message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String from = data.getString("from");
                    String message = data.getString("message");
                    String title = "Tin nhắn mới từ " + from;
                    notificationHelper.showNotification(title, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // Nhận thông báo (ví dụ: lời mời kết bạn)
        socket.on("receive notification", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                String type = data.optString("type", "Thông báo");
                String content = data.optString("content", "");
                String title = "Thông báo: " + type;
                notificationHelper.showNotification(title, content);
            }
        });
    }

    // Gửi lời mời kết bạn
    public void sendFriendRequest(String fromName, String toUserId, String content) {
        if (socket != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("toUserId", toUserId);
                data.put("fromName", fromName);
                data.put("type", "friend_request");
                data.put("content", content);
                socket.emit("send notification", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}