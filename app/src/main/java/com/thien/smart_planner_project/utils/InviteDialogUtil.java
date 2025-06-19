package com.thien.smart_planner_project.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.thien.smart_planner_project.Adapter.InviteEventAdapter;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.model.dto.SendMailInviteDTO;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteDialogUtil {
    private Context context;
    private List<Event> events, originalList;
    private User creator;
    private String emailTo;
    private String subject;
    public InviteDialogUtil(Context context, List<Event> events, String emailTo) {
        this.context = context;
        this.originalList = new ArrayList<>(events);
        this.events = events;
        this.emailTo = emailTo;
        subject  = "Lời mời gia nhập sự kiện";
    }

    public void showInviteDialog() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite_event);

        // Thiết lập kích thước dialog
        if(dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView btnClose = dialog.findViewById(R.id.imageButton);
        MaterialButton btnCancel = dialog.findViewById(R.id.cancel);
        MaterialButton btnInvite = dialog.findViewById(R.id.invite);
        MaterialCheckBox checkBoxAll = dialog.findViewById(R.id.checkBoxAll);
        SearchView searchView = dialog.findViewById(R.id.search_attendee);
        RecyclerView recyclerView = dialog.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        TextView amountChoose = dialog.findViewById(R.id.choose_event_amount);
        InviteEventAdapter adapter = new InviteEventAdapter(events);
        recyclerView.setAdapter(adapter);
        creator = SharedPrefManager.getInstance(dialog.getContext()).getUser();

        // Đóng dialog khi nhấn X hoặc Huỷ
        View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
        btnClose.setOnClickListener(dismissListener);
        btnCancel.setOnClickListener(dismissListener);
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Integer,Boolean> choosePosition = adapter.getChoosePosition();
                if(choosePosition.isEmpty()){
                    Toast.makeText(v.getContext(), "Bạn chưa chọn sự kiện nào để mời hết. Bạn có thể nhấn huỷ nếu không muốn mời", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Event> eventIds = choosePosition.keySet()
                        .stream()
                        .filter(choosePosition::get)
                        .map(i -> events.get(i))
                        .collect(Collectors.toList());
                ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                SendMailInviteDTO sendMailInviteDTO = new SendMailInviteDTO(creator.getEmail(),emailTo,subject,eventIds);
                Call<Void> call = apiService.sendEmailInvites(sendMailInviteDTO);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(v.getContext(), "Mời thành công", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(v.getContext(), "Mời không thành công. Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(v.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        checkBoxAll.addOnCheckedStateChangedListener(new MaterialCheckBox.OnCheckedStateChangedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedStateChangedListener(@NonNull MaterialCheckBox checkBox, int state) {
                boolean checked = (state == MaterialCheckBox.STATE_CHECKED);
                adapter.selectAll(checked);
                // cập nhật lại số lượng
                if (amountChoose != null) {
                    int count = adapter.getSelectedCount();
                    amountChoose.setText(String.format("Đã chọn: %d",count));
                }
            }
        });
        adapter.setOnCheckedCountChangeListener(new InviteEventAdapter.OnCheckedCountChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedCountChanged(int count) {
                amountChoose.setText(String.format("Đã chọn: %d",count));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        dialog.show();
    }
    @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
    public void filter(String newText) {
        if(newText == null || newText.isEmpty()){
            events.clear();
            events.addAll(originalList);
        }else {
            events.clear();
            events.addAll(originalList.stream()
                    .filter(e -> e.getName().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }
}
