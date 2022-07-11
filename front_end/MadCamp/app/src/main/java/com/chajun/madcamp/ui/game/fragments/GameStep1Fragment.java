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
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.logic.GameInfo;
import com.chajun.madcamp.ui.game.GameActivity;
import com.chajun.madcamp.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    private TextView[] moveCountTxts = new TextView[5];

    private int totalDeckCount = 7;
    private int totalCount = 0;
    private int[] moveCounts;

    private boolean isExpanded = false;

    private ViewGroup expandLayout1;
    private ViewGroup expandLayout2;


    Socket socket;
    Timer timer;
    int countDown = Constant.BUILD_DECK_MAX_COUNT_DOWN;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_step1, container, false);

        isExpanded = GameActivity.context.getIntent().getBooleanExtra(IntentKey.IS_EXPANDED, false);
        GameInfo.getInstance().gameType = isExpanded ? GameType.E : GameType.N;
        GameInfo.getInstance().totalTurn = GameActivity.context.getIntent().getIntExtra(IntentKey.NUM_TURNS, 123);
        GameInfo.getInstance().totalDeck = GameActivity.context.getIntent().getIntExtra(IntentKey.NUM_MOVES, 123);
        totalDeckCount = GameActivity.context.getIntent().getIntExtra(IntentKey.NUM_MOVES, 0);

        initViews(v);
        setViewPager2();

        setButtons();

        totalCountTxt.setText(Util.getTurnPerTotal(totalCount, totalDeckCount));

        socket = GameInfo.getInstance().socket;

        socket.on(SocketMsg.BUILD_DECK, onBuildDeck);
        socket.on(SocketMsg.START_GAME, onStartGame);

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
        moveCountTxts[3] = v.findViewById(R.id.game_step1_txt_lizard_count);
        moveCountTxts[4] = v.findViewById(R.id.game_step1_txt_spock_count);

        expandLayout1 = v.findViewById(R.id.game_step1_lizard_layout);
        expandLayout2 = v.findViewById(R.id.game_step1_spock_layout);

        moveCounts = new int[isExpanded ? 5 : 3];

        if (!isExpanded) {
            expandLayout1.setVisibility(View.GONE);
            expandLayout2.setVisibility(View.GONE);
        }
    }

    private void setButtons() {
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager2.getCurrentItem();

                if (moveCounts[pos] > 8 || totalCount == totalDeckCount) {
                    return;
                }

                moveCounts[pos]++;
                moveCountTxts[pos].setText(String.valueOf(moveCounts[pos]));

                totalCountTxt.setText(Util.getTurnPerTotal((++totalCount), totalDeckCount));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager2.getCurrentItem();

                if (moveCounts[pos] == 0) {
                    return;
                }

                moveCounts[pos]--;
                moveCountTxts[pos].setText(String.valueOf(moveCounts[pos]));

                totalCountTxt.setText(Util.getTurnPerTotal((--totalCount), totalDeckCount));
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
        if (isExpanded) {
            moveList.add(Move.L);
            moveList.add(Move.V);
        }

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
                        countDownTxt.setText(String.valueOf(countDown));

                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                countDown--;
                                GameActivity.context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        countDownTxt.setText(String.valueOf(countDown));
                                    }
                                });

                                if (countDown <= 0) {
                                    timer.cancel();
                                    timer = null;
                                }

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

    private final Emitter.Listener onStartGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (totalCount != totalDeckCount) {
                Random random = new Random();
                int moveMax = (GameInfo.getInstance().gameType == GameType.N) ? 3 : 5;
                for (int i = 0; i < totalDeckCount - totalCount; i++) {
                    int moveIndex;
                    do {
                        moveIndex = random.nextInt(moveMax);
                    } while (moveCounts[moveIndex] == totalDeckCount);

                    moveCounts[moveIndex]++;

                }
            }

            GameActivity.context.goStep2(moveCounts);
        }
    };

}
