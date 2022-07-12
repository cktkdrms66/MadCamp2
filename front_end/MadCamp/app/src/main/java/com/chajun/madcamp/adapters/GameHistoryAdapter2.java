package com.chajun.madcamp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chajun.madcamp.R;
import com.chajun.madcamp.data.model.response.GameHistory;
import com.chajun.madcamp.enums.GameResult;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.logic.GameAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;


public class GameHistoryAdapter2 extends RecyclerView.Adapter<GameHistoryAdapter2.GameHistoryViewHolder>{

    private List<GameHistory> gameHistoryList;
    private int userId;

    private Context context;

    public GameHistoryAdapter2(int userId, Context context) {
        gameHistoryList = new ArrayList<>();
        this.userId  = userId;
        this.context = context;
    }

    public void setItems(List<GameHistory> items) {
        gameHistoryList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_game_history_2, parent, false);
        return new GameHistoryViewHolder(context, view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GameHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        GameHistory gameHistory = gameHistoryList.get(position);

        boolean isHost = userId == gameHistory.getHostId();
        List<Move> hostMoves;
        List<Move> guestMoves;

        hostMoves = GameAlgorithm.convertStrToMoves(gameHistory.getHostMoves());
        guestMoves = GameAlgorithm.convertStrToMoves(gameHistory.getGuestMoves());

        List<GameResult> gameResults = new ArrayList<>();

        List<Move> myMoves = isHost ? hostMoves : guestMoves;
        List<Move> enemyMoves = isHost ? guestMoves : hostMoves;
        for (int i = 0; i < hostMoves.size(); i++) {

            gameResults.add(GameAlgorithm.getGameResult(myMoves.get(i), enemyMoves.get(i), gameHistory.getGameType() == GameType.E));
        }

        int winCount = gameResults.stream().filter(v -> v == GameResult.W).collect(Collectors.toList()).size();
        int loseCount = gameResults.stream().filter(v -> v == GameResult.L).collect(Collectors.toList()).size();

        GameResult gameResult;
        if (winCount > loseCount) {
            gameResult = GameResult.W;
            holder.gameResultTxt.setBackgroundResource(R.color.win);
        }
        else if (winCount == loseCount) {
            gameResult = GameResult.T;
            holder.gameResultTxt.setBackgroundResource(R.color.tie);
        }
        else {
            gameResult = GameResult.L;
            holder.gameResultTxt.setBackgroundResource(R.color.loss);
        }

        holder.gameResultTxt.setText(gameResult.toString());

        if (isHost) {
            if (gameResult == GameResult.W) {
                holder.hostLayout.setBackgroundResource(R.color.win_bg);
            } else if (gameResult == GameResult.L) {
                holder.hostLayout.setBackgroundResource(R.color.loss_bg);
            } else {
                holder.hostLayout.setBackgroundResource(R.color.tie_bg);
            }
            holder.guestLayout.setBackgroundColor(Color.WHITE);
        } else {
            holder.hostLayout.setBackgroundColor(Color.WHITE);
            if (gameResult == GameResult.W) {
                holder.guestLayout.setBackgroundResource(R.color.win_bg);
            } else if (gameResult == GameResult.L) {
                holder.guestLayout.setBackgroundResource(R.color.loss_bg);
            } else {
                holder.guestLayout.setBackgroundResource(R.color.tie_bg);
            }
        }

        LayoutInflater inflater = (LayoutInflater) holder.itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        holder.hostLayout.removeAllViews();
        holder.guestLayout.removeAllViews();

        for (int i = 0; i < hostMoves.size(); i++) {
            View hostMoveView = inflater.inflate(R.layout.item_move, null, false);
            View guestMoveView = inflater.inflate(R.layout.item_move, null, false);


            ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setImageResource(hostMoves.get(i).getDrawableId(gameHistory.getGameType() == GameType.E));
            ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setImageResource(guestMoves.get(i).getDrawableId(gameHistory.getGameType() == GameType.E));

            GameResult turnResult = GameAlgorithm.getGameResult(hostMoves.get(i), guestMoves.get(i), gameHistory.getGameType() == GameType.E);

            if (turnResult == GameResult.W) {
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.win, context.getTheme()));
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.win_bg);
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.loss, context.getTheme()));
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.loss_bg);
            } else if (turnResult == GameResult.L) {
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.loss, context.getTheme()));
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.loss_bg);
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.win, context.getTheme()));
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.win_bg);
            } else {
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.tie, context.getTheme()));
                ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.tie_bg);
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.tie, context.getTheme()));
                ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.tie_bg);
            }

            holder.hostLayout.addView(hostMoveView);
            holder.guestLayout.addView(guestMoveView);

        }

        List<Move> hostUnusedMoves = GameAlgorithm.convertStrToMoves(gameHistory.getHostUnusedMoves());
        List<Move> guestUnusedMoves = GameAlgorithm.convertStrToMoves(gameHistory.getGuestUnusedMoves());
        for (int i = 0; i < hostUnusedMoves.size(); i++) {
            View hostMoveView = inflater.inflate(R.layout.item_move, null, false);
            View guestMoveView = inflater.inflate(R.layout.item_move, null, false);

            ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setImageResource(hostUnusedMoves.get(i).getDrawableId(gameHistory.getGameType() == GameType.E));
            ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setImageResource(guestUnusedMoves.get(i).getDrawableId(gameHistory.getGameType() == GameType.E));

            ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.unused, context.getTheme()));
            ((CircleImageView) hostMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.unused_bg);
            ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setBorderColor(context.getResources().getColor(R.color.unused, context.getTheme()));
            ((CircleImageView) guestMoveView.findViewById(R.id.item_move_civ_move)).setCircleBackgroundColorResource(R.color.unused_bg);

            holder.hostLayout.addView(hostMoveView);
            holder.guestLayout.addView(guestMoveView);

        }

    }


    @Override
    public int getItemCount() {
        return gameHistoryList.size();
    }

    public class GameHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView gameResultTxt;
        ViewGroup background;
        LinearLayout hostLayout;
        LinearLayout guestLayout;

        GameHistoryViewHolder(Context context, View itemView) {
            super(itemView);

            gameResultTxt = itemView.findViewById(R.id.list_item_game_history_txt_result);
            background = itemView.findViewById(R.id.list_item_game_history_layout);
            hostLayout = itemView.findViewById(R.id.list_item_game_history_layout_host);
            guestLayout = itemView.findViewById(R.id.list_item_game_history_layout_guest);
        }
    }
}
