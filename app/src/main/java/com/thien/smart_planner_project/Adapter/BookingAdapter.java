package com.thien.smart_planner_project.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.thien.smart_planner_project.EventDetailActivity;
import com.thien.smart_planner_project.R;
import com.thien.smart_planner_project.callback.ApiCallback;
import com.thien.smart_planner_project.model.Booking;
import com.thien.smart_planner_project.model.Event;
import com.thien.smart_planner_project.network.ApiService;
import com.thien.smart_planner_project.network.RetrofitClient;

import java.util.List;

public class BookingAdapter extends BaseAdapter {
    private Context context;
    private List<Booking> bookings;
    private LayoutInflater inflater;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context;
        this.bookings = bookings;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
//        Log.d("BookingsSize ", String.valueOf(bookings.size()));
        return bookings.size();
    }

    @Override
    public Object getItem(int position) {
        return bookings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.booking_item, parent, false);
            holder = new ViewHolder();
            holder.imageP = convertView.findViewById(R.id.imageP);
            holder.idEvent = convertView.findViewById(R.id.idEvent);
            holder.dateEvent = convertView.findViewById(R.id.dateEvent);
            holder.dateBooking = convertView.findViewById(R.id.dateBooking);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            holder.detail=convertView.findViewById(R.id.detail);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu booking tại vị trí
        Booking booking = bookings.get(position);

        // Thiết lập dữ liệu cho View
        holder.idEvent.setText("Sự kiện: " + booking.getIdEvent());
        holder.dateEvent.setText("Ngày diễn ra: " + booking.getDate());
        holder.dateBooking.setText("Ngày đặt vé: " + booking.getTime());

        // Hiển thị ảnh (sử dụng Picasso nếu có URL QR code)
        if (booking.getUrlQR() != null && !booking.getUrlQR().isEmpty()) {
            Picasso.get().load(booking.getUrlQR()).placeholder(R.drawable.image_border).into(holder.imageP);
        } else {
            holder.imageP.setImageResource(R.drawable.image_border);
        }
        holder.imageP.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_image_preview);
            ImageView imgPreview = dialog.findViewById(R.id.imgPreview);
            imgPreview.setImageDrawable(holder.imageP.getDrawable());
            // Đóng dialog khi nhấn vùng ngoài
            dialog.setCancelable(true);
            dialog.show();
        });


        String imageUrl = "https://static.thenounproject.com/png/1247089-200.png";
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.button_drawable)
                .error(R.drawable.ic_launcher_background)
                .into(holder.detail);
        holder.detail.setOnClickListener(v -> {

            apiService.getEventByIdEvent(booking.getIdEvent()).enqueue(new ApiCallback<Event>() {
                @Override
                public void onSuccess(Event result) {
                    Event selectedEvent=result;
                    Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                    intent.putExtra("id", selectedEvent.getId());
                    intent.putExtra("name", selectedEvent.getName());
                    intent.putExtra("time", selectedEvent.getTime());
                    intent.putExtra("location", selectedEvent.getLocation());
                    intent.putExtra("image",selectedEvent.getImageUrl());
                    intent.putExtra("des", selectedEvent.getDescription());
                    intent.putExtra("seat", selectedEvent.getSeats());
                    intent.putExtra("date", selectedEvent.getDate());
                    intent.putExtra("uid", selectedEvent.getCreatorUid());
                    intent.putExtra("eventId", selectedEvent.get_id());
                    v.getContext().startActivity(intent);

                }
            });
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn hủy vé hay không?")
                    .setPositiveButton("Có", (dialog, which) -> {

                        apiService.deleteBooking(booking.getId()).enqueue(new ApiCallback<List<Booking>>() {
                            @Override
                            public void onSuccess(List<Booking> result) {
                                bookings.clear();
                                bookings.addAll(result);
                                notifyDataSetChanged();
                            }
                        });
                    })
                    .setNegativeButton("Không", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();

        });

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    static class ViewHolder {
        ImageView imageP;
        TextView idEvent;
        TextView dateEvent;
        TextView dateBooking;
        ImageButton btnDelete;
        ImageView detail;
    }


}
