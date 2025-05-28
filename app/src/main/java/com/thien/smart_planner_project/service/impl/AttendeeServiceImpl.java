package com.thien.smart_planner_project.service.impl;

import com.thien.smart_planner_project.model.Attendee;
import com.thien.smart_planner_project.service.AttendeeService;

import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AttendeeServiceImpl implements AttendeeService {
    private HttpLoggingInterceptor interceptor;
    private OkHttpClient client;
    private Retrofit retrofit;
    public AttendeeServiceImpl() {
        interceptor  = new HttpLoggingInterceptor();
        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .client(client) // 👈 Thêm dòng này
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public List<Attendee> getListAttendeeInEvent(String eventId) {
        return null;
    }
}
