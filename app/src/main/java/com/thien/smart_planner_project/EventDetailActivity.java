package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.thien.smart_planner_project.databinding.ActivityEventDetailBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityEventDetailBinding binding;

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

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        int seat = intent.getIntExtra("seat", 0);
        String des = intent.getStringExtra("des");
        String date = intent.getStringExtra("date");
        String img = intent.getStringExtra("image");



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