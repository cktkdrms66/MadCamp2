package com.chajun.madcamp.ui.game.fragments;

import android.app.Dialog;
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
import com.chajun.madcamp.data.model.request.GameCompleteRequest;
import com.chajun.madcamp.data.model.response.GameCompleteResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.GameResult;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.logic.GameAlgorithm;
import com.chajun.madcamp.logic.GameInfo;
import com.chajun.madcamp.ui.game.GameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameStep2Fragment extends Fragment {


    private TextView[] myMoveCountTxts = new TextView[5];
    private TextView[] enemyMoveCountTxts = new TextView[5];

    private View[] myMoves = new View[5];

    private TextView countDownTxt;

    private ImageView myMoveImg;
    private ImageView enemyMoveImg;

    private ViewGroup enemyExpandLayout1;
    private ViewGroup enemyExpandLayout2;
    private ViewGroup myExpandLayout1;
    private ViewGroup myExpandLayout2;

    private TextView boostingCountTxt;

    private ImageView[] gameResultImgs = new ImageView[7];

    private Move currentMyMove;
    private Move currentEnemyMove;

    private View boostingBtn;
    private boolean isBoosted;

    private ImageView[] enemyNormalImgs = new ImageView[3];
    private ImageView[] myNormalImgs = new ImageView[3];

    Timer timer;
    int countDown = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_step2, container, false);

        isBoosted = false;

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
        myMoveCountTxts[3] = v.findViewById(R.id.game_step2_txt_lizard_count_mine);
        myMoveCountTxts[4] = v.findViewById(R.id.game_step2_txt_spock_count_mine);

        myMoves[0] = v.findViewById(R.id.game_step2_my_rock);
        myMoves[1] = v.findViewById(R.id.game_step2_my_scissor);
        myMoves[2] = v.findViewById(R.id.game_step2_my_paper);
        myMoves[3] = v.findViewById(R.id.game_step2_my_lizard);
        myMoves[4] = v.findViewById(R.id.game_step2_my_spock);

        enemyMoveCountTxts[0] = v.findViewById(R.id.game_step2_txt_rock_count_enemy);
        enemyMoveCountTxts[1] = v.findViewById(R.id.game_step2_txt_scissor_count_enemy);
        enemyMoveCountTxts[2] = v.findViewById(R.id.game_step2_txt_paper_count_enemy);
        enemyMoveCountTxts[3] = v.findViewById(R.id.game_step2_txt_lizard_count_enemy);
        enemyMoveCountTxts[4] = v.findViewById(R.id.game_step2_txt_spock_count_enemy);

        countDownTxt = v.findViewById(R.id.game_step2_txt_countdown);
        //currentTurnPerTotalTxt = v.findViewById(R.id.game_step2_txt_turn_per_total);

        boostingCountTxt = v.findViewById(R.id.game_step2_txt_boosting_count);
        boostingCountTxt.setText("1");

        gameResultImgs[0] = v.findViewById(R.id.game_step2_img_game_result_0);
        gameResultImgs[1] = v.findViewById(R.id.game_step2_img_game_result_1);
        gameResultImgs[2] = v.findViewById(R.id.game_step2_img_game_result_2);
        gameResultImgs[3] = v.findViewById(R.id.game_step2_img_game_result_3);
        gameResultImgs[4] = v.findViewById(R.id.game_step2_img_game_result_4);
        gameResultImgs[5] = v.findViewById(R.id.game_step2_img_game_result_5);
        gameResultImgs[6] = v.findViewById(R.id.game_step2_img_game_result_6);

        myExpandLayout1 = v.findViewById(R.id.game_step2_my_lizard);
        myExpandLayout2 = v.findViewById(R.id.game_step2_my_spock);

        enemyExpandLayout1 = v.findViewById(R.id.game_step2_enemy_ex_layout1);
        enemyExpandLayout2 = v.findViewById(R.id.game_step2_enemy_ex_layout2);

        enemyNormalImgs[0] = v.findViewById(R.id.step2_enemy_rock_icon);
        enemyNormalImgs[1] = v.findViewById(R.id.step2_enemy_scissor_icon);
        enemyNormalImgs[2] = v.findViewById(R.id.step2_enemy_paper_icon);

        myNormalImgs[0] = v.findViewById(R.id.step2_my_rock_icon);
        myNormalImgs[1] = v.findViewById(R.id.step2_my_scissor_icon);
        myNormalImgs[2] = v.findViewById(R.id.step2_my_paper_icon);

        if (GameInfo.getInstance().gameType == GameType.N) {
            myExpandLayout1.setVisibility(View.GONE);
            myExpandLayout2.setVisibility(View.GONE);
            enemyExpandLayout1.setVisibility(View.GONE);
            enemyExpandLayout2.setVisibility(View.GONE);
        } else {
            enemyNormalImgs[0].setImageResource(R.drawable.icon_rock_expand);
            enemyNormalImgs[1].setImageResource(R.drawable.icon_scissor_expand);
            enemyNormalImgs[2].setImageResource(R.drawable.icon_paper_expand);

            myNormalImgs[0].setImageResource(R.drawable.icon_rock_expand);
            myNormalImgs[1].setImageResource(R.drawable.icon_scissor_expand);
            myNormalImgs[2].setImageResource(R.drawable.icon_paper_expand);
        }

        for (int i = 0; i < GameInfo.getInstance().totalTurn; i++) {
            gameResultImgs[i].setVisibility(View.VISIBLE);
        }

        myMoveImg = v.findViewById(R.id.game_step2_img_move_mine);
        enemyMoveImg = v.findViewById(R.id.game_step2_img_move_enemy);

        boostingBtn = v.findViewById(R.id.game_step2_btn_boosting);
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
        GameInfo.getInstance().socket.on(SocketMsg.GAME_COMPLETE, onGameComplete);

        int[] moveCounts = GameInfo.getInstance().myMoveCounts;
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (int i = 0; i < moveCounts.length; i++) {
            array.put(moveCounts[i]);
        }
        object.put("arr", array);

        GameInfo.getInstance().socket.emit(SocketMsg.INIT_GAME, GameInfo.getInstance().totalTurn, GameInfo.getInstance().gameType.toString(),
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
                        GameInfo.getInstance().myMoveCounts[currentMyMove.getOriginalIndex()]++;
                        myMoveCountTxts[currentMyMove.getOriginalIndex()].setText(String.valueOf(GameInfo.getInstance().myMoveCounts[currentMyMove.getOriginalIndex()]));
                    }

                    GameInfo.getInstance().myMoveCounts[finalI]--;

                    currentMyMove = Move.getMove(finalI);

                    myMoveCountTxts[finalI].setText(String.valueOf(GameInfo.getInstance().myMoveCounts[finalI]));


                    myMoveImg.setImageResource(currentMyMove.getDrawableId(GameInfo.getInstance().gameType == GameType.E));

                    if (isBoosted) {
                        boostingCountTxt.setText("1");
                        changeImgOriginal(myMoveImg);
                    }

                    isBoosted = false;
                }
            });
        }

        boostingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("boost");

                if (currentMyMove == null) return;

                if (isBoosted) {
                    System.out.println(currentMyMove);
                    boostingCountTxt.setText("1");
                    currentMyMove = Move.getOriginalMove(currentMyMove);
                    changeImgOriginal(myMoveImg);
                } else {
                    boostingCountTxt.setText("0");
                    System.out.println(currentMyMove);
                    currentMyMove = Move.getBoostedMove(currentMyMove);
                    changeImgSizeBigger(myMoveImg);
                }

                isBoosted = !isBoosted;

                myMoveImg.setImageResource(currentMyMove.getDrawableId(GameInfo.getInstance().gameType == GameType.E));

            }
        });
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

    private void changeImgSizeBigger(ImageView imageView) {
        imageView.getLayoutParams().height += 150;
        imageView.getLayoutParams().width += 150;
    }

    private void changeImgOriginal(ImageView imageView) {
        imageView.getLayoutParams().height -= 150;
        imageView.getLayoutParams().width -= 150;
    }

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
                        myMoveImg.setImageResource(currentMyMove.getDrawableId(GameInfo.getInstance().gameType == GameType.E));

                        if (currentMyMove.index > 5) {
                            boostingBtn.setClickable(false);
                        }
                        GameInfo.getInstance().socket.emit(SocketMsg.COMPARE_START, currentMyMove.index, GameInfo.getInstance().isHost);
                    }
                });
            } else {
                if (currentMyMove.index > 5) {
                    boostingBtn.setClickable(false);
                }
                GameInfo.getInstance().socket.emit(SocketMsg.COMPARE_START, currentMyMove.index, GameInfo.getInstance().isHost);
            }
        }
    };

    private final Emitter.Listener onTurnResult = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            isBoosted = false;

            System.out.println("on Turn result!!");

            int myIndex = GameInfo.getInstance().isHost ? 0 : 1;
            int enemyIndex = GameInfo.getInstance().isHost ? 1 : 0;

            currentMyMove = Move.getMove((int) args[myIndex]);
            currentEnemyMove = Move.getMove((int) args[enemyIndex]);

            GameInfo.getInstance().enemyMoveCounts[currentEnemyMove.getOriginalIndex()]--;

            GameActivity.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCountClickable(false);
                    enemyMoveImg.setImageResource(currentEnemyMove.getDrawableId(GameInfo.getInstance().gameType == GameType.E));

                    if (currentEnemyMove.index >= 5) {
                        changeImgSizeBigger(enemyMoveImg);
                    }

                    int gameResultVal = (int) args[2];
                    if (myIndex == 1) {
                        gameResultVal = ((int) args[2]) * (-1);
                    }

                    System.out.println("game reuslt :: " + gameResultVal);

                    GameInfo.getInstance().myMoves.add(currentMyMove);
                    GameInfo.getInstance().enemyMoves.add(currentEnemyMove);
                    GameInfo.getInstance().gameResults.add(GameResult.getGameResult(gameResultVal));

                    gameResultImgs[GameInfo.getInstance().currentTurn - 1]
                            .setImageResource(GameResult.getGameResult(gameResultVal).getDrawableId());

                    //currentTurnPerTotalTxt.setText(Util.getTurnPerTotal(GameInfo.getInstance().currentTurn,
                            //GameInfo.getInstance().totalTurn));

                    Toast.makeText(GameActivity.context, GameResult.getGameResult(gameResultVal).getStringId(), Toast.LENGTH_SHORT).show();
                    setTexts();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //딜레이 후 시작할 코드 작성
                            System.out.println("next turn!!");
                            enemyMoveImg.setImageResource(R.drawable.icon_unknown);
                            myMoveImg.setImageResource(R.drawable.icon_unknown);

                            if (currentMyMove.index >= 5) {
                                changeImgOriginal(myMoveImg);

                            }

                            if (currentEnemyMove.index >= 5) {
                                changeImgOriginal(enemyMoveImg);
                            }

                            GameInfo.getInstance().socket.emit(SocketMsg.NEXT_TURN);


                            currentMyMove = null;
                            currentEnemyMove = null;

                        }
                    }, 4000);
                }
            });



        }
    };

    private final Emitter.Listener onGameComplete = new Emitter.Listener() {
        @Override
        public void call(Object... args) {


            GameActivity.context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("game complete");

                    GameInfo.getInstance().disconnectSocket();

                    int gameResult = 0;
                    int ratingChange = 0;

                    GameInfo gameInfo = GameInfo.getInstance();

                    for (int i = 0; i < gameInfo.gameResults.size(); i++) {
                        gameResult += gameInfo.gameResults.get(i).val;
                    }

                    ratingChange = gameResult * 10;

                    if (gameResult > 0) gameResult = 1;
                    else if (gameResult == 0) gameResult = 0;
                    else gameResult = -1;

                    //this.hostUnusedMoves = hostUnusedMoves;
                    //        this.guestUnusedMoves = guestUnusedMoves;
                    //
                    List<Move> hostUnusedMoves = new ArrayList<>();
                    List<Move> guestUnusedMoves = new ArrayList<>();

                    for (int i = 0; i < gameInfo.myMoveCounts.length; i++) {
                        System.out.println("myMoveCounts " + i + " " + gameInfo.myMoveCounts[i]);
                        System.out.println("enemyMoveCounts " + i + " " + gameInfo.enemyMoveCounts[i]);
                        for (int j = 0; j < gameInfo.myMoveCounts[i]; j++) {
                            hostUnusedMoves.add(Move.getMove(i));
                        }
                        for (int j = 0; j < gameInfo.enemyMoveCounts[i]; j++) {
                            guestUnusedMoves.add(Move.getMove(i));
                        }
                    }

                    if (GameInfo.getInstance().isHost) {
                        System.out.println("host가 보냅니다");
                        System.out.println(GameAlgorithm.convertMovesToStr(gameInfo.myMoves) + "\n" +
                                GameAlgorithm.convertMovesToStr(gameInfo.enemyMoves) + "\n" +
                                GameAlgorithm.convertMovesToStr(hostUnusedMoves) + "\n" +
                                GameAlgorithm.convertMovesToStr(guestUnusedMoves));
                        Repository.getInstance().completeGame(new GameCompleteRequest(
                                gameInfo.roomId,
                                AppData.userId,
                                (int) args[0],
                                gameResult,
                                ratingChange,
                                gameInfo.totalTurn,
                                gameInfo.gameType.toString(),
                                gameInfo.totalDeck,
                                GameAlgorithm.convertMovesToStr(gameInfo.myMoves),
                                GameAlgorithm.convertMovesToStr(gameInfo.enemyMoves),
                                GameAlgorithm.convertMovesToStr(hostUnusedMoves),
                                GameAlgorithm.convertMovesToStr(guestUnusedMoves)
                        ), new Callback<GameCompleteResponse>() {
                            @Override
                            public void onResponse(Call<GameCompleteResponse> call, Response<GameCompleteResponse> response) {
                                System.out.println("game 기록 완료");

                            }

                            @Override
                            public void onFailure(Call<GameCompleteResponse> call, Throwable t) {

                            }
                        });

                    }

                    Dialog dialog;
                    dialog = new Dialog(GameActivity.context);
                    dialog.setContentView(R.layout.dialog_game_result);

                    String ratingStr = ratingChange >= 0 ? "+" + ratingChange : String.valueOf(ratingChange);

                    ((TextView) dialog.findViewById(R.id.dialog_game_result_title)).setText(GameResult.getGameResult(gameResult).getStringId());
                    ((TextView) dialog.findViewById(R.id.dialog_game_result_my_score)).setText(String.valueOf((int) args[GameInfo.getInstance().isHost ? 1 : 2]));
                    ((TextView) dialog.findViewById(R.id.dialog_game_result_plus_score)).setText(ratingStr);
                    dialog.findViewById(R.id.dialog_game_result_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                            GameActivity.context.finish();
                        }
                    });

                    dialog.show();

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
