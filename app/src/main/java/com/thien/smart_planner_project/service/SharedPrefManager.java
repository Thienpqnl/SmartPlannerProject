package com.thien.smart_planner_project.service;

import android.content.SharedPreferences;
import android.content.Context;
import com.google.gson.Gson;
import com.thien.smart_planner_project.model.User;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "smart_planner_prefs";
    private static final String KEY_USER = "user";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUser(User user) {
        String userJson = gson.toJson(user);
        sharedPreferences.edit().putString(KEY_USER, userJson).apply();
    }

    public User getUser() {
        String userJson = sharedPreferences.getString(KEY_USER, null);
        return userJson != null ? gson.fromJson(userJson, User.class) : null;
    }

    public void clearUser() {
        sharedPreferences.edit().remove(KEY_USER).apply();
    }
}
