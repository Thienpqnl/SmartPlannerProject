package com.thien.smart_planner_project;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.thien.smart_planner_project.Fragment.ChatBoxFragment;
import com.thien.smart_planner_project.Fragment.InviteFriendFragment;
import com.thien.smart_planner_project.Fragment.ListFriendFragment;

public class MainChatActivity extends AppCompatActivity {
    private TextView tabChat, tabFriends, tabInviteFriends;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);

        back = findViewById(R.id.back_main_chat);
        back.setOnClickListener(v ->{
            finish();
        });
        tabChat = findViewById(R.id.chat_box);
        tabFriends = findViewById(R.id.list_friend);
        tabInviteFriends = findViewById(R.id.invite_friend);


        int activeColor = ContextCompat.getColor(this, R.color.blue_bold);
        int activeTextColor = ContextCompat.getColor(this, R.color.background_light);

        tabChat.setTextColor(activeTextColor);
        tabChat.setBackgroundColor(activeColor);
        showFragment(new ChatBoxFragment());

        tabChat.setOnClickListener(v -> {
            resetTabColors();
            tabChat.setTextColor(activeTextColor);
            tabChat.setBackgroundColor(activeColor);
            showFragment(new ChatBoxFragment());
        });
        tabFriends.setOnClickListener(v -> {
            resetTabColors();
            tabFriends.setTextColor(activeTextColor);
            tabFriends.setBackgroundColor(activeColor);
            showFragment(new ListFriendFragment());
        });
        tabInviteFriends.setOnClickListener(v -> {
            resetTabColors();
            tabInviteFriends.setTextColor(activeTextColor);
            tabInviteFriends.setBackgroundColor(activeColor);
            showFragment(new InviteFriendFragment());
        });
    }
    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    private void resetTabColors() {
        int inactiveColor = ContextCompat.getColor(this, R.color.background_light);
        tabChat.setBackgroundColor(inactiveColor);
        tabFriends.setBackgroundColor(inactiveColor);
        tabInviteFriends.setBackgroundColor(inactiveColor);

        tabChat.setTextColor(Color.BLACK);
        tabFriends.setTextColor(Color.BLACK);
        tabInviteFriends.setTextColor(Color.BLACK);
    }
}