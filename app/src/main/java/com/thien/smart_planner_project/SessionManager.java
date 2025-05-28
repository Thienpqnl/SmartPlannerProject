package com.thien.smart_planner_project;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Lưu thông tin đăng nhập
    public void createLoginSession(String userId, String userName) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.apply(); // Lưu thay đổi
    }

    // Lấy trạng thái đăng nhập
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Lấy thông tin người dùng
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    // Xóa session (đăng xuất)
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }
}
