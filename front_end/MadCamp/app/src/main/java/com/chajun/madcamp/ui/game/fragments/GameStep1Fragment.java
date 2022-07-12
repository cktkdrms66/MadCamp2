package com.chajun.madcamp.ui.game.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.GameHistoryAdapter;
import com.chajun.madcamp.adapters.GameStep1ViewPagerAdapter;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.response.GameHistory;
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

    private RecyclerView enemyRecyclerView;

    private Button plusBtn;
    private Button minusBtn;

    private ViewPager2 viewPager2;

    private ProgressBar progress;

    private TextView[] moveCountTxts = new TextView[5];

    private int totalDeckCount = 7;
    private int totalCount = 0;
    private int[] moveCounts;
    private int enemyId;

    private boolean isExpanded = false;

    private ViewGroup expandLayout1;
    private ViewGroup expandLayout2;

    private ImageView rockImg;
    private ImageView scissorImg;
    private ImageView paperImg;

    private GameHistoryAdapter adapter;


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

        rockImg = v.findViewById(R.id.step1_rock_icon);
        scissorImg = v.findViewById(R.id.step1_scissor_icon);
        paperImg = v.findViewById(R.id.step1_paper_icon);

        progress = v.findViewById(R.id.step1_progress);

        enemyRecyclerView = v.findViewById(R.id.step1_recyclerview_enemy);

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
        } else {
            rockImg.setImageResource(R.drawable.icon_rock_expand);
            scissorImg.setImageResource(R.drawable.icon_scissor_expand);
            paperImg.setImageResource(R.drawable.icon_paper_expand);
        }
    }

    private void setEnemyRecyclerView() {
        Toast errorToast = Toast.makeText(GameActivity.context, R.string.list_refresh_error, Toast.LENGTH_SHORT);

        System.out.println("enemyId :: "+ enemyId);
        Repository.getInstance().getGameHistoryList(enemyId, new Callback<List<GameHistory>>() {
            @Override
            public void onResponse(Call<List<GameHistory>> call, Response<List<GameHistory>> response) {
                try {
                    if (response.isSuccessful()) {
                        progress.setVisibility(View.GONE);
                        enemyRecyclerView.setVisibility(View.VISIBLE);

                        adapter = new GameHistoryAdapter(enemyId, GameActivity.context);
                        enemyRecyclerView.setLayoutManager(new LinearLayoutManager(GameActivity.context, RecyclerView.VERTICAL, false));
                        enemyRecyclerView.setAdapter(adapter);
                        adapter.setItems(response.body());
                    } else {
                        errorToast.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorToast.show();
                }

            }

            @Override
            public void onFailure(Call<List<GameHistory>> call, Throwable t) {
                errorToast.show();
            }
        });

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
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(50));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);


        List<Move> moveList = new ArrayList<>();
        moveList.add(Move.R);
        moveList.add(Move.S);
        moveList.add(Move.P);
        if (isExpanded) {
            moveList.add(Move.L);
            moveList.add(Move.V);
        }

        viewPager2.setAdapter(new GameStep1ViewPagerAdapter(moveList));
        viewPager2.setCurrentItem(isExpanded ? 2 : 1);
    }



    private final Emitter.Listener onBuildDeck = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            GameActivity.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (GameInfo.getInstance().isHost) {
                        Toast.makeText(GameActivity.context, R.string.join_enemy_1, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GameActivity.context, R.string.join_enemy_2, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            System.out.println("build deck start!");
            System.out.println("mine :: " + AppData.userId);
            System.out.println("args :: 0 " + (int) args[0]);
            System.out.println("args :: 1 " + (int) args[1]);
            enemyId = 0;
            for (int i = 0; i < args.length; i++ ) {
                if ((int) args[i] != AppData.userId) {
                    enemyId = (int) args[i];
                    break;
                }
            }

            Repository.getInstance().getUserInfo(enemyId, new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    System.out.println("enemy id ::: " + enemyId);
                    if (response.isSuccessful()) {
                        User enemyUser = response.body();
                        enemyNameTxt.setText(enemyUser.getName());
                        countDownTxt.setText(String.valueOf(countDown));

                        setEnemyRecyclerView();

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
                    System.out.println("fail!!!!");
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
