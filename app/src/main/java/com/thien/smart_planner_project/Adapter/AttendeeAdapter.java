package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {
    private List<UserAttendeeDTO> userAttendeeDTOS;
    private String eventId;
    private String type;
    private final String content = """
        Xin chào %s,

        Bạn đã bị xoá khỏi sự kiện bởi người tổ chức.
        Lý do: %s

        Nếu có thắc mắc, vui lòng liên hệ ban tổ chức để biết thêm chi tiết.
        Trân trọng,
        Ban tổ chức sự kiện
        """;

    public AttendeeAdapter(List<UserAttendeeDTO> userAttendeeDTOS, String eventId, String type) {
        this.userAttendeeDTOS = userAttendeeDTOS;
        this.eventId = eventId;
        this.type = type;
    }
    public interface OnCountChangeListener {
        void onCountChanged(int count);
    }
    private OnCountChangeListener countChangeListener;
    public void setOnCountChangeListener(OnCountChangeListener listener) {
        this.countChangeListener = listener;
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
        holder.popup_menu_img.setVisibility(this.type.equals("Danh sách người tham gia") ? View.GONE : View.VISIBLE);
        // Trong onBindViewHolder
        holder.popup_menu_img.setOnClickListener(v -> {
            showPopupMenu(v,position,userAttendeeDTO,type);
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

    @SuppressLint("NotifyDataSetChanged")
    public void setType(String type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public String getType() {
        return type;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void showPopupMenu(View v, int position, UserAttendeeDTO userAttendeeDTO, String type){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        if (type.equals("Danh sách người đặt vé")){
            popup.getMenuInflater().inflate(R.menu.popup_menu_attendee, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.delete_attendee) {
                    Context context = v.getContext();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Nhập lý do xoá người này");

                    // Sử dụng view custom
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_detete_attendee, null);
                    final EditText input = dialogView.findViewById(R.id.edit_reason);
                    final CheckBox checkBoxRestrict = dialogView.findViewById(R.id.checkbox_restrict);

                    builder.setView(dialogView);

                    builder.setPositiveButton("Xoá", null); // Để null để tự xử lý sau
                    builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Xử lý nút Xoá
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                        String reason = input.getText().toString().trim();
                        if (reason.isEmpty()) {
                            Toast.makeText(context, "Hãy nhập lý do cho việc xoá người này ra khỏi sự kiện", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean isRestricted = checkBoxRestrict.isChecked();
                        // Truyền thêm biến isRestricted để xử lý hạn chế người này
                        deleteAttendee(userAttendeeDTO.getUser(), eventId, context, reason, position, isRestricted);
                        dialog.dismiss();
                    });
                    return true;
                }
                return false;
            });
        }else{
            popup.getMenuInflater().inflate(R.menu.popup_menu_restricted, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.delete_attendee_out_restricted_list) {
                    deleteUserOutRestrictedList(v.getContext(),userAttendeeDTO);
                }
                return false;
            });
        }
        popup.show();
    }
    public void deleteUserOutRestrictedList(Context context,UserAttendeeDTO userAttendeeDTO){
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.removeUserFromRestrictedList(eventId,userAttendeeDTO.getUser().getUserId());
        call.enqueue(new Callback<Void>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(context, "Xoá không thành công", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Đã xoá người dùng này ra khỏi danh sách hạn chế", Toast.LENGTH_SHORT).show();
                userAttendeeDTOS.remove(userAttendeeDTO);
                notifyDataSetChanged();
                if(countChangeListener != null){
                    countChangeListener.onCountChanged(userAttendeeDTOS.size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void deleteAttendee(User user, String eventId, Context context, String reason, int deletePosition, boolean isRestricted) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // 1. Xoá attendee
        Call<Void> deleteCall = apiService.deleteAttendee(eventId, user.getUserId());
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // 2. Gửi email thông báo
                    sendEmailAfterDelete(user, eventId, context, reason, deletePosition, isRestricted);
                } else {
                    Toast.makeText(context, "Xoá không thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server khi xoá attendee", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailAfterDelete(User user, String eventId, Context context, String reason, int deletePosition, boolean isRestricted) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        EmailRequest emailRequest = new EmailRequest(
                SharedPrefManager.getInstance(context).getUser().getEmail(),
                user.getEmail(),
                "Thông báo",
                String.format(content, user.getEmail(), reason)
        );

        Call<Void> emailCall = apiService.sendEmailAboutDeteleBookTicket(emailRequest);
        emailCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thông báo cho người dùng này", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Không gửi được email thông báo", Toast.LENGTH_SHORT).show();
                }

                userAttendeeDTOS.remove(deletePosition);
                notifyItemRemoved(deletePosition);
                if (countChangeListener != null) {
                    countChangeListener.onCountChanged(userAttendeeDTOS.size());
                }
                if (isRestricted) {
                    putUserToRestrictedList(user, eventId, context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server khi gửi email", Toast.LENGTH_SHORT).show();

                // Vẫn thực hiện restricted nếu cần
                if (isRestricted) {
                    putUserToRestrictedList(user, eventId, context);
                }
            }
        });
    }

    private void putUserToRestrictedList(User user, String eventId, Context context) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> restrictCall = apiService.putUserInRestrictedList(eventId, user.getUserId());
        restrictCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã thêm người dùng này vào danh sách hạn chế", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Thêm người dùng này vào danh sách hạn chế thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server khi hạn chế", Toast.LENGTH_SHORT).show();
            }
        });
    }

}