package com.thien.smart_planner_project.callback;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiCallback<T> implements Callback<T> {

    public ApiCallback(){
    }
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            onSuccess(response.body());
        } else {
            String errorDetails = parseErrorDetails(response.errorBody());
            onError("Lỗi khi xử lý yêu cầu: " + errorDetails);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onError("Lỗi kết nối: " + t.getMessage());
    }
    public abstract void onSuccess(T result);
    public void onError(String errorMessage) {
        Log.e("err", "Lỗi khi gọi api: " + errorMessage);
    }
    private String parseErrorDetails(ResponseBody errorBody) {
        try {
            if (errorBody != null) {
                return errorBody.string();
            }
        } catch (IOException e) {
            Log.e("err", "Không thể đọc chi tiết lỗi từ errorBody", e);
        }
        return "Không rõ chi tiết lỗi.";
    }
}
