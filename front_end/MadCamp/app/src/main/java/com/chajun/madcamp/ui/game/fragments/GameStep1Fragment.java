package com.chajun.madcamp.ui.game.fragments;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameStep1Fragment extends Fragment {

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
    int count = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_step1, container, false);

        initViews(v);
        setViewPager2();

        setButtons();

        totalCountTxt.setText(totalCount  + " / " + totalDeckCount);

        socket = GameActivity.context.socket;

        socket.on(SocketMsg.BUILD_DECK, onBuildDeck);
        socket.on(SocketMsg.START_TURN, onStartTurn);

        return v;
    }



    private void initViews(View v) {
        enemyNameTxt = v.findViewById(R.id.step1_txt_enemy_name);
        enemyStateTxt = v.findViewById(R.id.step1_txt_enemy_state);
        countDownTxt = v.findViewById(R.id.step1_txt_count_down);
        viewPager2 = v.findViewById(R.id.step1_viewpager2);

        totalCountTxt = v.findViewById(R.id.step1_txt_total_count);

        plusBtn = v.findViewById(R.id.step1_plug_btn);
        minusBtn = v.findViewById(R.id.step1_minus_btn);

        moveCountTxts[0] = v.findViewById(R.id.game_step1_txt_rock_count);
        moveCountTxts[1] = v.findViewById(R.id.game_step1_txt_scissor_count);
        moveCountTxts[2] = v.findViewById(R.id.game_step1_txt_paper_count);
    }

    private void setButtons() {
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager2.getCurrentItem();

                int count = Integer.valueOf(moveCountTxts[pos].getText().toString());

                if (count > 8 || totalCount == totalDeckCount) {
                    return;
                }

                count++;
                moveCountTxts[pos].setText(String.valueOf(count));

                totalCountTxt.setText((++totalCount)+" / " + totalDeckCount);
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager2.getCurrentItem();

                int count = Integer.valueOf(moveCountTxts[pos].getText().toString());

                if (count == 0) {
                    return;
                }

                count--;
                moveCountTxts[pos].setText(String.valueOf(count));

                totalCountTxt.setText((--totalCount)+" / " + totalDeckCount);
            }
        });
    }

    private void setViewPager2() {
        viewPager2.setClipToPadding(false);

        viewPager2.setPadding(100, 0, 100, 0);

        List<Move> moveList = new ArrayList<>();
        moveList.add(Move.R);
        moveList.add(Move.S);
        moveList.add(Move.P);

        viewPager2.setAdapter(new GameStep1ViewPagerAdapter(moveList));
    }



    private final Emitter.Listener onBuildDeck = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("build deck start!");
            System.out.println("mine :: " + AppData.userId);
            System.out.println("args :: 0 " + (int) args[0]);
            System.out.println("args :: 1 " + (int) args[1]);
            int enemyId = 0;
            for (int i = 0; i < args.length; i++ ) {
                if ((int) args[i] != AppData.userId) {
                    enemyId = (int) args[i];
                    break;
                }
            }

            Repository.getInstance().getUserInfo(enemyId, new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        User enemyUser = response.body();
                        enemyNameTxt.setText(enemyUser.getName());
                        enemyStateTxt.setText(R.string.enemy_state_waiting);
                        countDownTxt.setText(String.valueOf(count));

                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                if (count <= 0) {
                                    timer.cancel();
                                    timer = null;
                                    return;
                                }
                                count--;
                                GameActivity.context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        countDownTxt.setText(String.valueOf(count));
                                    }
                                });

                            }
                        };
                        timer = new Timer();
                        timer.schedule(task, 0, 1000);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    };

    private final Emitter.Listener onStartTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            GameActivity.context.goStep2();
        }
    };

}
