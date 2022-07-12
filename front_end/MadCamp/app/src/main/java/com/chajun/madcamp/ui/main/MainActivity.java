package com.chajun.madcamp.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.ui.main.fragments.HomeFragment;
import com.chajun.madcamp.ui.main.fragments.RankListFragment;
import com.chajun.madcamp.ui.main.fragments.RoomListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.kakao.sdk.common.KakaoSdk;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {

    private View randomMatchBtn;
    private View myInfoBtn;
    private View roomListBtn;
    private View rankListBtn;
    private View makeRoomBtn;


    private BottomNavigationView mainBottomNav;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        initView();
        setBottomNav();


    }

    private void initView() {
        mainBottomNav = findViewById(R.id.main_bottom_nav);
    }

    private void setBottomNav() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame_layout, homeFragment).commitAllowingStateLoss();

        mainBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        transaction.replace(R.id.main_frame_layout, homeFragment).commitAllowingStateLoss();
                        break;
                    case R.id.menu_room:
                        transaction.replace(R.id.main_frame_layout, roomListFragment).commitAllowingStateLoss();
                        break;
                    case R.id.menu_rank:
                        transaction.replace(R.id.main_frame_layout, rankListFragment).commitAllowingStateLoss();
                        break;

                }
                return true;
            }
        });
    }
}