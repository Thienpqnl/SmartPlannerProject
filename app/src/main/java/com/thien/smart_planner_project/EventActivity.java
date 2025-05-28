package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.adapter.EventAdapter;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    private Button filterBtn;

    private EditText searchEdt;

    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_event);
        filterBtn = findViewById(R.id.btnFilter);
        searchEdt = findViewById(R.id.searchEdt);
        recyclerView = findViewById(R.id.recyclerViewEvent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setAdapter(eventAdapter);

        loadEvents();


        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Event> newEvList = new ArrayList<>();
                  for (int i = 0; i < eventList.size(); i++) {
                      if (eventList.get(i).getName().toLowerCase().contains(searchEdt.getText().toString().toLowerCase())) {
                          newEvList.add(eventList.get(i));

                      }

                  }
                eventAdapter = new EventAdapter(newEvList);
                recyclerView.setAdapter(eventAdapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, FilterActivity.class);

                startActivity(intent);
            }
        });
    }

    private void loadEvents() {
        if (!eventList.isEmpty()) {
            return;
        }
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Event>> call = apiService.getAllEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EventActivity.this, "Không lấy được sự kiện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(EventActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
