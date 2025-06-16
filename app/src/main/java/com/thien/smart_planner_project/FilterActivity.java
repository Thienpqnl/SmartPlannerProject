package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.utils.DatePickerUtils;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    CheckBox checkbox1_10, checkbox11_50, checkbox51_100, checkbox101_plus;
    EditText dateFrom, dateTo, timeFrom, timeTo;
    TextView  myLocate, chooseLocate, locateView;
    RadioGroup radioGroup;
    Button btnFilter;
    @Override
    protected void onCreate(Bundle getBundle) {
        super.onCreate(getBundle);
        setContentView(R.layout.filter_layout);

        dateFrom = findViewById(R.id.dateFromFilter);
        dateTo = findViewById(R.id.dateToFilter);
        timeFrom = findViewById(R.id.timeFrom);
        timeTo = findViewById(R.id.timeTo);

        checkbox1_10 = findViewById(R.id.checkbox_1_10);
        checkbox11_50 = findViewById(R.id.checkbox_11_50);
        checkbox51_100 = findViewById(R.id.checkbox_51_100);
        checkbox101_plus = findViewById(R.id.checkbox_101_plus);


        myLocate = findViewById(R.id.myLocate);
        chooseLocate = findViewById(R.id.chooseLocate);
        locateView = findViewById(R.id.locateView);


        radioGroup = findViewById(R.id.radioGroup);
        btnFilter = findViewById(R.id.getFilter);

        dateFrom.setOnClickListener(v -> DatePickerUtils.showDatePicker(this, dateFrom, null));
        dateTo.setOnClickListener(v -> DatePickerUtils.showDatePicker(this, dateTo, null));
        timeFrom.setOnClickListener(v ->DatePickerUtils.showTimePicker(this,timeFrom,null));
        timeTo.setOnClickListener(v ->DatePickerUtils.showTimePicker(this,timeTo,null));

        btnFilter.setOnClickListener(v -> {
            // Lấy giá trị từ giao diện
            String fromDate = dateFrom.getText().toString();
            String toDate = dateTo.getText().toString();
            String fromTime = timeFrom.getText().toString();
            String toTime = timeTo.getText().toString();

            List<String> seatRanges = new ArrayList<>();
            if (checkbox1_10.isChecked()) seatRanges.add("1-10");
            if (checkbox11_50.isChecked()) seatRanges.add("11-50");
            if (checkbox51_100.isChecked()) seatRanges.add("51-100");
            if (checkbox101_plus.isChecked()) seatRanges.add("101+");

            int selectedId = radioGroup.getCheckedRadioButtonId();
            String eventType = "";
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                eventType = selectedRadio.getText().toString();
            }

            String myLocation = myLocate.getText().toString();
            String chosenLocation = chooseLocate.getText().toString();
            String location = chosenLocation.isEmpty() ? myLocation : chosenLocation;

            // Gọi hàm xử lý
            handleFilter(fromDate, toDate, fromTime, toTime, seatRanges, eventType, location);
        });


    }
    private void handleFilter(String fromDate, String toDate, String fromTime, String toTime,
                              List<String> seatRanges, String eventType, String location) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("fromDate", fromDate);
        resultIntent.putExtra("toDate", toDate);
        resultIntent.putExtra("fromTime", fromTime);
        resultIntent.putExtra("toTime", toTime);
        resultIntent.putStringArrayListExtra("seatRanges", new ArrayList<>(seatRanges));
        resultIntent.putExtra("eventType", eventType);
        resultIntent.putExtra("location", location);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    }
