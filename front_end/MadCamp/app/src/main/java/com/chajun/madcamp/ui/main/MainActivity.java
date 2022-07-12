package com.chajun.madcamp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.request.RandomMatchRequest;
import com.chajun.madcamp.data.model.response.RandomMatchResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.ui.game.GameActivity;
import com.chajun.madcamp.ui.room.AddRoomActivity;
import com.chajun.madcamp.ui.room.RoomListActivity;
import com.chajun.madcamp.ui.userinfo.RankListActivity;
import com.chajun.madcamp.ui.userinfo.UserInfoActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private View myInfoBtn;
    private View roomListBtn;
    private View rankListBtn;
    private View createRoomBtn;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        initView();
        setBtns();

    }

    private void initView() {
        myInfoBtn = findViewById(R.id.main_card_my_info);
        roomListBtn = findViewById(R.id.main_card_room_list);
        rankListBtn = findViewById(R.id.main_card_rank_list);
        createRoomBtn = findViewById(R.id.main_card_create_room);
    }

    private void setBtns() {

        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra(IntentKey.USER_ID, AppData.userId);
                startActivity(intent);
            }
        });

        roomListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
                startActivity(intent);
            }
        });

        rankListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RankListActivity.class);
                startActivity(intent);
            }
        });

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddRoomActivity.class);
                startActivity(intent);
            }
        });
    }


}