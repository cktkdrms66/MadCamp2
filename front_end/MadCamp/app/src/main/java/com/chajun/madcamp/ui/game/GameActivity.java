package com.chajun.madcamp.ui.game;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.game.fragments.GameStep1Fragment;


import java.util.Arrays;
import java.util.stream.Collectors;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity {

    private GameStep1Fragment fragment1 = new GameStep1Fragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    int roomId;

    public static GameActivity context;

    public Socket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        context = this;

        roomId = getIntent().getIntExtra(IntentKey.ROOM_ID, -1);

        if (roomId == -1) {
            finish();
        } else {

            initViews();
            try {
                socket = IO.socket(Constant.BASE_URL);
                socket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);
                socket.connect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (socket != null) {
            socket.disconnect();
        }
        super.onDestroy();

    }

    private void initViews() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.game_layout, fragment1).commitAllowingStateLoss();

    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("connect good!!!");
            socket.emit(SocketMsg.JOIN_ROOM, roomId, AppData.userId);
        }
    };


}
