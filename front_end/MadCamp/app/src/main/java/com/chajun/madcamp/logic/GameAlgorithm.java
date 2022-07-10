package com.chajun.madcamp.logic;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.chajun.madcamp.enums.GameResult;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameAlgorithm {

    private static GameResult[][] table = new GameResult[][]{
            {GameResult.T, GameResult.W, GameResult.L, GameResult.W, GameResult.L, GameResult.L},
            {GameResult.L, GameResult.T, GameResult.W, GameResult.L, GameResult.W, GameResult.L},
            {GameResult.W, GameResult.L, GameResult.T, GameResult.L, GameResult.L, GameResult.W},
            {GameResult.L, GameResult.W, GameResult.W, GameResult.T, GameResult.W, GameResult.L},
            {GameResult.W, GameResult.L, GameResult.W, GameResult.L, GameResult.T, GameResult.W},
            {GameResult.W, GameResult.W, GameResult.L, GameResult.W, GameResult.L, GameResult.T},
            };

    public static GameResult getGameResult(Move mine, Move enemy, boolean isExpandMode) {
        return table[mine.index][enemy.index];
    }

    public static List<Move> convertStrToMoves(String str) {

        return Arrays.stream(str.split(" ")).map(v -> Move.valueOf(v)).collect(Collectors.toList());
    }

    public static String convertMovesToStr(List<Move> moves) {
        String result = "";
        for (int i = 0; i < moves.size(); i++) {
            result = moves.get(i).toString() + " ";
        }
        return result.trim();
    }


}
