package com.chajun.madcamp.ui.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.SocketMsg;


import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameStep1Activity extends AppCompatActivity {

    Socket socket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_step1);

        try {
            socket = IO.socket(Constant.BASE_URL);
            socket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);

            socket.on(SocketMsg.START_GAME, onStartGame);
            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("connect good!!!");
        }
    };

    private final Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("qweqwejqwipejeipfjpwejfi");
        }
    };
}
