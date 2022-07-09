package com.chajun.madcamp.ui.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;


import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameStep1Activity extends AppCompatActivity {

    Socket socket;
    int roomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_step1);

        roomId = getIntent().getIntExtra(IntentKey.ROOM_ID, -1);

        if (roomId == -1) {
            finish();
        } else {
            try {
                socket = IO.socket(Constant.BASE_URL);
                socket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);
                socket.on(SocketMsg.START_GAME, onStartGame);
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

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("connect good!!!");
            Map<String, Object> map = new HashMap<>();
            map.put("userId", AppData.userId);
            map.put("roomId", roomId);
            socket.emit(SocketMsg.JOIN_ROOM, map);
        }
    };

    private final Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("start game");


        }
    };
}
