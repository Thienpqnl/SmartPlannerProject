package com.thien.smart_planner_project.Controller;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private final FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveEvent(String imageUrl, String name, String date, String time, Timestamp Timestamp,
                          String location, double latitude, double longitude, int seats, String description, FirestoreCallback callback) {
        Map<String, Object> event = new HashMap<>();
        event.put("imageUrl", imageUrl);
        event.put("name", name);
        event.put("date", date);
        event.put("timestamp", Timestamp);
        event.put("time", time);
        event.put("location", location);
        event.put("latitude", latitude);
        event.put("longitude", longitude);
        event.put("seats", seats);
        event.put("description", description);

        db.collection("Events").add(event)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId(); // Lấy eventID tự động được tạo
                    System.out.println("Event ID tự động: " + eventId);

                    // Có thể cập nhật thêm nếu cần lưu lại eventID trong tài liệu
                    documentReference.update("eventID", eventId);

                    callback.onSuccess(eventId);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

    }

    public interface FirestoreCallback {
        void onSuccess(String eventId);
        void onFailure(String error);
    }


}

