package com.chajun.madcamp.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.ui.userinfo.UserInfoActivity;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class HomeFragment extends Fragment {

    private Button randomMatchStartBtn;
    private Button myInfoBtn;
    Socket socket;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(v);
        setMyInfoBtn();

        return v;
    }

    private void initViews(View v) {
        randomMatchStartBtn = v.findViewById(R.id.home_btn_random_match_start);
        myInfoBtn = v.findViewById(R.id.home_btn_my_info);


    }

    private void setMyInfoBtn() {
        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HomeFragment.this.getActivity(), UserInfoActivity.class);
//                intent.putExtra(IntentKey.USER_ID, AppData.userId);
//                startActivity(intent);
                try {
                    socket = IO.socket(Constant.BASE_URL);
                    socket.on(io.socket.client.Socket.EVENT_CONNECT, onConnect);

                    socket.on(SocketMsg.START_GAME, onStartGame);
                    socket.connect();

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
