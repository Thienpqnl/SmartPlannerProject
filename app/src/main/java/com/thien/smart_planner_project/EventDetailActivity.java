package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.thien.smart_planner_project.databinding.ActivityEventDetailBinding;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityEventDetailBinding binding;

    private User user;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        TextView txtName = findViewById(R.id.detailName);
        TextView txtTime = findViewById(R.id.detailTime);
        TextView txtLocation = findViewById(R.id.detailLocal);
        TextView txtDate = findViewById(R.id.detailDate);
        TextView txtSeat = findViewById(R.id.detailSeat);
        TextView txtDes = findViewById(R.id.detailDes);
        ImageView imgEvent = findViewById(R.id.detailImg);
        TextView creator = findViewById(R.id.creator);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        int seat = intent.getIntExtra("seat", 0);
        String des = intent.getStringExtra("des");
        String date = intent.getStringExtra("date");
        String img = intent.getStringExtra("image");
        String uid = intent.getStringExtra("uid");

        Intent intent1 = new Intent(EventDetailActivity.this, UserDetailActivity.class);
        txtName.setText("Ten su kien: " + name);
        txtTime.setText(time);
        txtLocation.setText(location);
        txtDate.setText(date);
        txtSeat.setText(seat + "");
        txtDes.setText(des);
        Glide.with(this)
                .load(img)
                .into(imgEvent);

        if (isPastDate(date)) {
            txtName.setTextColor(Color.GRAY);
            txtName.setText(name + "(Đã diễn ra)");
        }

        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<User> call = apiService.getUserById(uid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = response.body();
                assert user != null;
                intent1.putExtra("name", user.getName());
                intent1.putExtra("email", user.getEmail());
                intent1.putExtra("local", user.getLocation());
                intent1.putExtra("role", user.getRole());
                intent1.putExtra("uid", user.getUserId());
                creator.setText(user.getName());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e ("err","loi khi goi api: "  + t);
            }
        });
     }
    public boolean isPastDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        try {
            Date inputDate = sdf.parse(dateStr);
            Date today = new Date(); // ngày hiện tại
            return inputDate.before(today); // trả về true nếu là ngày quá khứ
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // lỗi parse thì cho là không phải ngày quá khứ
        }
    }

}