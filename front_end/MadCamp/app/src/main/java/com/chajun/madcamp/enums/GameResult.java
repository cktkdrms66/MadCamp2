package com.chajun.madcamp.enums;

import com.chajun.madcamp.R;

public enum GameResult {
    W(1), L(-1), T(0);

    public final int val;

    GameResult(int val) { this.val = val; }

    public static GameResult getGameResult(int val) {
        switch (val) {
            case 1:
                return W;
            case 0:
                return T;
            case -1:
                return L;
        }

        return W;
    }

    public int getDrawableId() {
        switch (this) {
            case W:
                return R.drawable.icon_win;
            case T:
                return R.drawable.icon_tie;
            case L:
                return R.drawable.icon_lose;
            default:
                return 123123;
        }
    }

    public int getStringId() {
        switch (this) {
            case W:
                return R.string.win;
            case T:
                return R.string.tie;
            case L:
                return R.string.loss;
            default:
                return 123123;
        }
    }

}
