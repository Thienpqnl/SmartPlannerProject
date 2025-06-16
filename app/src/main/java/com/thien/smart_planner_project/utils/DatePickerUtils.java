package com.thien.smart_planner_project.utils;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;

public class DatePickerUtils {

    public interface OnDateSelectedListener {
        void onDateSelected(long timestamp);
    }
    public interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }
    public static void showDatePicker(Context context, EditText targetEditText, OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDate now = LocalDate.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        targetEditText.setText(formattedDate);

                        // Gọi callback nếu cần xử lý timestamp
                        if (listener != null) {
                            long timestamp = convertToTimestamp(selectedDay, selectedMonth + 1, selectedYear);
                            listener.onDateSelected(timestamp);
                        }

                }, year, month, day);

        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, EditText targetEditText, OnTimeSelectedListener listener) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    targetEditText.setText(time);

                    if (listener != null) {
                        listener.onTimeSelected(hourOfDay, minute1);
                    }
                },
                hour, minute, false
        );

        timePickerDialog.show();
    }
    private static long convertToTimestamp(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        return cal.getTimeInMillis();
    }
}
