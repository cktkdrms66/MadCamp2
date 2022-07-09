package com.chajun.madcamp.ui.game.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.GameStep1ViewPagerAdapter;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.ui.game.GameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameStep2Fragment extends Fragment {

    private TextView enemyNameTxt;
    private TextView enemyStateTxt;
    private TextView countDownTxt;

    private TextView totalCountTxt;

    private Button plusBtn;
    private Button minusBtn;

    private ViewPager2 viewPager2;

    private TextView[] moveCountTxts = new TextView[3];

    private int totalDeckCount = 7;
    private int totalCount = 0;

    Socket socket;
    Timer timer;
    int count = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_step2, container, false);



        return v;
    }



    private void initViews(View v) {

    }



}
