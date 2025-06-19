package com.thien.smart_planner_project.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thien.smart_planner_project.EventDetailActivity;
import com.thien.smart_planner_project.R;

public class QRFragment extends DialogFragment {

    private static final String ARG_QR_URL = "qr_url";

    public static QRFragment newInstance(String qrUrl) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QR_URL, qrUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        ImageView qrImageView = view.findViewById(R.id.qrImageView);
        Button btnDownload = view.findViewById(R.id.btnDownload);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        String qrUrl = getArguments() != null ? getArguments().getString(ARG_QR_URL) : null;

        if (qrUrl != null) {
            Glide.with(this)
                    .load(qrUrl)
                    .error(R.drawable.ic_launcher_background)
                    .into(qrImageView);
        }

        btnDownload.setOnClickListener(v -> {
            if (getActivity() instanceof EventDetailActivity) {
                ((EventDetailActivity) getActivity()).downloadQRImage(qrUrl);
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onQRDialogDismiss();
        }
    }




    public interface OnQRDialogDismissListener {
        void onQRDialogDismiss();
    }

    private OnQRDialogDismissListener dismissListener;

    public void setOnQRDialogDismissListener(OnQRDialogDismissListener listener) {
        this.dismissListener = listener;
    }
}
