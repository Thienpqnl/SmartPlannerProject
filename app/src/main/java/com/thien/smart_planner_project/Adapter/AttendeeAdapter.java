package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.UserPreviewActivity;
import com.thien.smart_planner_project.model.EmailRequest;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.UserAttendeeDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {
    private List<UserAttendeeDTO> userAttendeeDTOS;
    private String eventId;
    private boolean isBooking;
    private final String content = """
        Xin chào %s,

        Bạn đã bị xoá khỏi sự kiện bởi người tổ chức.
        Lý do: %s

        Nếu có thắc mắc, vui lòng liên hệ ban tổ chức để biết thêm chi tiết.
        Trân trọng,
        Ban tổ chức sự kiện
        """;

    public AttendeeAdapter(List<UserAttendeeDTO> userAttendeeDTOS, String eventId, boolean isBooking) {
        this.userAttendeeDTOS = userAttendeeDTOS;
        this.eventId = eventId;
        this.isBooking = isBooking;
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView attendeeName, attendeeTime;
        ImageView popup_menu_img;
        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            attendeeName = itemView.findViewById(R.id.attendee_name);
            attendeeTime = itemView.findViewById(R.id.time_attend);
            popup_menu_img = itemView.findViewById(R.id.popup_menu_attendee);
        }
    }

    @NonNull
    @Override
    public AttendeeAdapter.AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        UserAttendeeDTO userAttendeeDTO = userAttendeeDTOS.get(position);
        holder.attendeeName.setText(userAttendeeDTO.getUser().getName());
        holder.attendeeTime.setText(userAttendeeDTO.getBookingDate());
        holder.popup_menu_img.setVisibility(isBooking ? View.VISIBLE : View.GONE);
        // Trong onBindViewHolder
        holder.popup_menu_img.setOnClickListener(v -> {
            if (!isBooking) return;
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.popup_menu_attendee, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.delete_attendee) {
                    Context context = v.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Nhập lý do xoá người này");
                    final EditText input = new EditText(context);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    input.setHint("Nhập lý do...");
                    builder.setView(input);
                    builder.setPositiveButton("Xoá", (dialog, which) -> {
                        String reason = input.getText().toString().trim();
                        int deletePosition = holder.getAdapterPosition();
                        deleteAttendee(userAttendeeDTO.getUser(), eventId, context, reason, deletePosition);
                    });
                    builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());
                    builder.show();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAbsoluteAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                User user = userAttendeeDTOS.get(pos).getUser();
                Intent intent = new Intent(v.getContext(), UserPreviewActivity.class);
                intent.putExtra("uid", user.getUserId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userAttendeeDTOS.size();
    }

    private void deleteAttendee(User user, String eventId, Context context, String reason,int deletePosition){
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteAttendee(eventId,user.getUserId());

        call.enqueue(new Callback<Void>() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                EmailRequest emailRequest = new EmailRequest(
                        SharedPrefManager.getInstance(context).getUser().getEmail(),
                        user.getEmail(),
                        "Thông báo",
                        String.format(content, user.getEmail(), reason)
                );

                Call<Void> callApi = apiService.sendEmailAboutDeteleBookTicket(emailRequest);
                callApi.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Toast.makeText(context, "Xoá thành công và đã thông báo cho user", Toast.LENGTH_SHORT).show();
                        userAttendeeDTOS.remove(deletePosition);
                        notifyItemRemoved(deletePosition);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setBooking(boolean booking) {
        isBooking = booking;
        notifyDataSetChanged();
    }

    public boolean isBooking() {
        return isBooking;
    }
}