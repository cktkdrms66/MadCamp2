package com.chajun.madcamp.ui.game.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.enums.GameResult;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.logic.GameInfo;
import com.chajun.madcamp.ui.game.GameActivity;
import com.chajun.madcamp.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class GameStep2Fragment extends Fragment {


    private TextView[] myMoveCountTxts = new TextView[3];
    private TextView[] enemyMoveCountTxts = new TextView[3];

    private View[] myMoves = new View[3];

    private TextView countDownTxt;

    private ImageView myMoveImg;
    private ImageView enemyMoveImg;

    private ImageView[] gameResultImgs = new ImageView[4];

    private Move currentMyMove;
    private Move currentEnemyMove;

    Timer timer;
    int countDown = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_step2, container, false);

        initViews(v);
        setTexts();

        setBtns();

        try {
            initGame();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }


    private void initViews(View v) {
        myMoveCountTxts[0] = v.findViewById(R.id.game_step2_txt_rock_count_mine);
        myMoveCountTxts[1] = v.findViewById(R.id.game_step2_txt_scissor_count_mine);
        myMoveCountTxts[2] = v.findViewById(R.id.game_step2_txt_paper_count_mine);

        myMoves[0] = v.findViewById(R.id.game_step2_my_rock);
        myMoves[1] = v.findViewById(R.id.game_step2_my_scissor);
        myMoves[2] = v.findViewById(R.id.game_step2_my_paper);

        enemyMoveCountTxts[0] = v.findViewById(R.id.game_step2_txt_rock_count_enemy);
        enemyMoveCountTxts[1] = v.findViewById(R.id.game_step2_txt_scissor_count_enemy);
        enemyMoveCountTxts[2] = v.findViewById(R.id.game_step2_txt_paper_count_enemy);

        countDownTxt = v.findViewById(R.id.game_step2_txt_countdown);
        //currentTurnPerTotalTxt = v.findViewById(R.id.game_step2_txt_turn_per_total);

        gameResultImgs[0] = v.findViewById(R.id.game_step2_img_game_result_0);
        gameResultImgs[1] = v.findViewById(R.id.game_step2_img_game_result_1);
        gameResultImgs[2] = v.findViewById(R.id.game_step2_img_game_result_2);
        gameResultImgs[3] = v.findViewById(R.id.game_step2_img_game_result_3);

        myMoveImg = v.findViewById(R.id.game_step2_img_move_mine);
        enemyMoveImg = v.findViewById(R.id.game_step2_img_move_enemy);
    }

    private void setTexts() {
        for (int i = 0; i < GameInfo.getInstance().myMoveCounts.length; i++) {
            myMoveCountTxts[i].setText(String.valueOf(GameInfo.getInstance().myMoveCounts[i]));
            enemyMoveCountTxts[i].setText(String.valueOf(GameInfo.getInstance().enemyMoveCounts[i]));
        }
    }


    private void initGame() throws JSONException {
        GameInfo.getInstance().socket.on(SocketMsg.DECK_INFO, onDeckInfo);
        GameInfo.getInstance().socket.on(SocketMsg.START_TURN, onStartTurn);
        GameInfo.getInstance().socket.on(SocketMsg.SUBMIT_MOVES, onSubmitMoves);
        GameInfo.getInstance().socket.on(SocketMsg.TURN_RESULT, onTurnResult);

        int[] moveCounts = GameInfo.getInstance().myMoveCounts;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0; i < moveCounts.length; i++) {
            array.put(moveCounts[i]);
        }
        object.put("arr", array);

        GameInfo.getInstance().socket.emit(SocketMsg.INIT_GAME, GameInfo.getInstance().totalTurn,
                object, GameInfo.getInstance().isHost ? 1 : 0);
    }

    private void setBtns() {
        for (int i = 0; i < myMoveCountTxts.length; i++) {
            int finalI = i;
            myMoves[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GameInfo.getInstance().myMoveCounts[finalI] == 0) {
                        Toast.makeText(GameActivity.context, R.string.prevent_count_0_move, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (currentMyMove != null && currentMyMove.index == finalI) {
                        return;
                    }

                    if (currentMyMove != null) {
                        GameInfo.getInstance().myMoveCounts[currentMyMove.index]++;
                        myMoveCountTxts[currentMyMove.index].setText(String.valueOf(GameInfo.getInstance().myMoveCounts[currentMyMove.index]));
                    }

                    GameInfo.getInstance().myMoveCounts[finalI]--;

                    currentMyMove = Move.getMove(finalI);

                    myMoveCountTxts[finalI].setText(String.valueOf(GameInfo.getInstance().myMoveCounts[finalI]));


                    myMoveImg.setImageResource(currentMyMove.getDrawableId());
                }
            });
        }
    }


    private final Emitter.Listener onDeckInfo = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                System.out.println("qweqw");
                System.out.println(args[0].toString());
                System.out.println(args[1].toString());
                JSONObject object = (JSONObject) args[GameInfo.getInstance().isHost ? 1 : 0];
                JSONArray array = object.getJSONArray("arr");
                int[] enemyMoveCounts = new int[array.length()];
                System.out.println(array);
                for (int i = 0; i < array.length(); i++) {
                    enemyMoveCounts[i] = array.getInt(i);
                }
                GameInfo.getInstance().enemyMoveCounts = enemyMoveCounts;


                GameActivity.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTexts();
                        System.out.println("emit next turn");
                        GameInfo.getInstance().socket.emit(SocketMsg.NEXT_TURN);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private Emitter.Listener onStartTurn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            GameInfo.getInstance().currentTurn++;

            countDown = Constant.TURN_MAX_COUNT_DOWN;

            GameActivity.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCountClickable(true);
                    //currentTurnPerTotalTxt.setText(Util.getTurnPerTotal(GameInfo.getInstance().currentTurn,
                            //GameInfo.getInstance().totalTurn));
                }
            });
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
    };

    private final Emitter.Listener onSubmitMoves = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("onSubmitMoves");

            if (currentMyMove == null) {
                GameActivity.context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        do {
                            currentMyMove = Move.getMove(new Random().nextInt(GameInfo.getInstance().gameType == GameType.N ? 3 : 5));
                        }
                        while (GameInfo.getInstance().myMoveCounts[currentMyMove.index] == 0);


                        myMoveCountTxts[currentMyMove.index].setText(String.valueOf(--GameInfo.getInstance().myMoveCounts[currentMyMove.index]));
                        myMoveImg.setImageResource(currentMyMove.getDrawableId());
                        GameInfo.getInstance().socket.emit(SocketMsg.COMPARE_START, currentMyMove.index, GameInfo.getInstance().isHost);
                    }
                });
            } else {
                GameInfo.getInstance().socket.emit(SocketMsg.COMPARE_START, currentMyMove.index, GameInfo.getInstance().isHost);
            }
        }
    };

    private final Emitter.Listener onTurnResult = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            System.out.println("on Turn result!!");

            int myIndex = GameInfo.getInstance().isHost ? 0 : 1;
            int enemyIndex = GameInfo.getInstance().isHost ? 1 : 0;

            currentMyMove = Move.getMove((int) args[myIndex]);
            currentEnemyMove = Move.getMove((int) args[enemyIndex]);

            GameInfo.getInstance().enemyMoveCounts[currentEnemyMove.index]--;


            GameActivity.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCountClickable(false);
                    enemyMoveImg.setImageResource(currentEnemyMove.getDrawableId());

                    int gameResultVal = (int) args[2];
                    if (myIndex == 1) {
                        gameResultVal = ((int) args[2]) * (-1);
                    }
                    GameInfo.getInstance().gameResults.add(GameResult.getGameResult(gameResultVal));

                    gameResultImgs[GameInfo.getInstance().currentTurn - 1]
                            .setImageResource(GameResult.getGameResult(gameResultVal).getDrawableId());

                    //currentTurnPerTotalTxt.setText(Util.getTurnPerTotal(GameInfo.getInstance().currentTurn,
                            //GameInfo.getInstance().totalTurn));

                    currentMyMove = null;
                    currentEnemyMove = null;

                    Toast.makeText(GameActivity.context, GameResult.getGameResult(gameResultVal).getStringId(), Toast.LENGTH_SHORT).show();
                    setTexts();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //딜레이 후 시작할 코드 작성
                            System.out.println("next turn!!");
                            enemyMoveImg.setImageResource(R.drawable.questiomark);
                            myMoveImg.setImageResource(R.drawable.questiomark);
                            GameInfo.getInstance().socket.emit(SocketMsg.NEXT_TURN);

                        }
                    }, 4000);
                }
            });



        }
    };

    private void setCountClickable(boolean isClickable) {
        for (int i = 0; i < myMoves.length; i++) {
            myMoves[i].setClickable(isClickable);
        }

    }


}
