package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.thien.smart_planner_project.AttendeeListBookTicketActivity;
import com.thien.smart_planner_project.UserDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.UserPreviewActivity;
import com.thien.smart_planner_project.model.EmailRequest;
import com.thien.smart_planner_project.model.User;
import com.thien.smart_planner_project.model.UserBookingDTO;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;
import com.thien.smart_planner_project.service.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {
    private static List<UserBookingDTO> userBookingDTOS;
    private String eventId;
    private final String content = """
        Xin chào %s,

        Bạn đã bị xoá khỏi sự kiện bởi người tổ chức.
        Lý do: %s

        Nếu có thắc mắc, vui lòng liên hệ ban tổ chức để biết thêm chi tiết.
        Trân trọng,
        Ban tổ chức sự kiện
        """;
    public AttendeeAdapter(List<UserBookingDTO> userBookingDTOS) {
        this.userBookingDTOS = userBookingDTOS;
    }

    public AttendeeAdapter(List<UserBookingDTO> userBookingDTOS, String eventId) {
        this.userBookingDTOS = userBookingDTOS;
        this.eventId = eventId;
    }

    public static class AttendeeViewHolder extends RecyclerView.ViewHolder{
        TextView attendeeName, attendeeTime;
        ImageView popup_menu_img;
        public AttendeeViewHolder(@NonNull View itemView,String eventId) {
            super(itemView);
            attendeeName = itemView.findViewById(R.id.attendee_name);
            attendeeTime = itemView.findViewById(R.id.time_attend);
            popup_menu_img = itemView.findViewById(R.id.popup_menu_attendee);
            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                User user = userBookingDTOS.get(position).getUser();
                if(eventId == null){
                    popup_menu_img.setVisibility(View.INVISIBLE);
                }
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(v.getContext(), UserPreviewActivity.class);
                    intent.putExtra("uid",user.getUserId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    @NonNull
    @Override
    public AttendeeAdapter.AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee, parent, false);
        return new AttendeeViewHolder(view,eventId);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
       UserBookingDTO userBookingDTO = userBookingDTOS.get(position);
       holder.attendeeName.setText(userBookingDTO.getUser().getName());
       holder.attendeeTime.setText(userBookingDTO.getBookingDate());
       holder.popup_menu_img.setOnClickListener(v ->{
           PopupMenu popup = new PopupMenu(v.getContext(), v);
           popup.getMenuInflater().inflate(R.menu.popup_menu_attendee, popup.getMenu());
           popup.setOnMenuItemClickListener(item -> {
               if(item.getItemId() == R.id.infor_attendee){
                   Intent intent = new Intent(v.getContext(), UserPreviewActivity.class);
                   intent.putExtra("uid",userBookingDTO.getUser().getUserId());
                   v.getContext().startActivity(intent);
                   return true;
               }
               if (item.getItemId() == R.id.delete_attendee) {
                   // Hiển thị dialog cho người dùng nhập lý do xoá
                   Context context = v.getContext();
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle("Nhập lý do xoá người này");

                   // Tạo EditText để nhập lý do
                   final EditText input = new EditText(context);
                   input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                   input.setHint("Nhập lý do...");
                   builder.setView(input);

                   builder.setPositiveButton("Xoá", (dialog, which) -> {
                       String reason = input.getText().toString().trim();
                       // Gọi hàm xoá và truyền lý do vào
                       deleteAttendee(userBookingDTO.getUser(), eventId, context, reason);
                   });
                   builder.setNegativeButton("Huỷ", (dialog, which) -> dialog.cancel());

                   builder.show();
                   return true;
               }
               return false;
           });
           popup.show();
       });
    }

    @Override
    public int getItemCount() {
        return userBookingDTOS.size();
    }
    private void deleteAttendee(User user, String eventId, Context context, String reason){
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteAttendee(eventId,user.getUserId());
        call.enqueue(new Callback<Void>() {
            @SuppressLint({"NotifyDataSetChanged", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                EmailRequest emailRequest = new EmailRequest(SharedPrefManager.getInstance(context).getUser().getEmail()
                                                                ,user.getEmail()
                                                                , "Thông báo"
                                                                ,String.format(content,user.getEmail(),reason));

                Call<Void> callApi = apiService.sendEmailAboutDeteleBookTicket(emailRequest);
                callApi.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        Toast.makeText(context, "Xoá thành công và đã thông báo cho user", Toast.LENGTH_SHORT).show();
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
}
