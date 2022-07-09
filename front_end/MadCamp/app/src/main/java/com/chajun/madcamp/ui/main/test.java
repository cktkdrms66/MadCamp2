package com.chajun.madcamp.ui.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


class QweActivity extends AppCompatActivity {


    TextView textView;
    Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.home_btn_my_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //소켓 연결
                try {

                    // new Repository().qwe();
                    socket = IO.socket("http://172.10.5.52:443");
                    socket.on(Socket.EVENT_CONNECT, onConnect);

                    socket.on("qwe", onConnect2);
                    socket.connect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.home_btn_my_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.close();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("qwe", "qwe");
            }
        });
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            socket.emit("qwe", "hi");
        }
    };

    private Emitter.Listener onConnect2 = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println(args);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(args[0].toString());
                }
            });
        }
    };




}