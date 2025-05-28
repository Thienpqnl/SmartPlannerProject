package com.thien.smart_planner_project;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FilterActivity extends AppCompatActivity {

    CheckBox checkbox1_10, checkbox11_50, checkbox51_100, checkbox101_plus;
    @Override
    protected void onCreate(Bundle getBundle) {


        super.onCreate(getBundle);

        setContentView(R.layout.filter_layout);

        TextView day1 = findViewById(R.id.dateFromFilter);

        TextView day2 = findViewById(R.id.dateToFilter);

        TextView time1 = findViewById(R.id.timeFrom);

        TextView time2 = findViewById(R.id.timeTo);

        checkbox1_10 = findViewById(R.id.checkbox_1_10);
        checkbox11_50 = findViewById(R.id.checkbox_11_50);
        checkbox51_100 = findViewById(R.id.checkbox_51_100);
        checkbox101_plus = findViewById(R.id.checkbox_101_plus);


        RadioGroup radioGroup = findViewById(R.id.radioGroup);

    }
}
