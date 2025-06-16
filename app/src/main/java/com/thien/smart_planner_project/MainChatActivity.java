package com.thien.smart_planner_project;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.thien.smart_planner_project.Fragment.ChatBoxFragment;
import com.thien.smart_planner_project.Fragment.InviteFriendFragment;
import com.thien.smart_planner_project.Fragment.ListFriendsFragment;

public class MainChatActivity extends AppCompatActivity {
    private TextView tabChat, tabFriends, tabInviteFriends;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);


        tabChat = findViewById(R.id.chat_box);
        tabFriends = findViewById(R.id.list_friend);
        tabInviteFriends = findViewById(R.id.invite_friend);

        showFragment(new ChatBoxFragment());
        int activeColor = ContextCompat.getColor(this, R.color.blue_bold);
        int activeTextColor = ContextCompat.getColor(this, R.color.background_light);

        tabChat.setOnClickListener(v -> {
            tabChat.setTextColor(activeTextColor);
            tabChat.setBackgroundColor(activeColor);
            resetTabColors();
            showFragment(new ChatBoxFragment());
        });
        tabFriends.setOnClickListener(v -> {
            tabFriends.setTextColor(activeTextColor);
            tabFriends.setBackgroundColor(activeColor);
            resetTabColors();
            showFragment(new ListFriendsFragment());
        });
        tabInviteFriends.setOnClickListener(v -> {
            tabInviteFriends.setTextColor(activeTextColor);
            tabInviteFriends.setBackgroundColor(activeColor);
            resetTabColors();
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
        tabFriends.setBackgroundColor(Color.BLACK);
        tabInviteFriends.setBackgroundColor(Color.BLACK);
    }
}