package com.chajun.madcamp.enums;

import com.chajun.madcamp.R;

public enum Move {
    R(0), S(1), P(2), MR(3), MS(4), MP(5);

    public final int index;

    Move(int index) { this.index = index; }

    public static Move getMove(int index) {
        switch (index) {
            case 0:
                return R;
            case 1:
                return S;
            case 2:
                return P;
        }

        //TODO
        return Move.MP;
    }

    public int getDrawableId() {
        switch (this) {
            case R:
                return com.chajun.madcamp.R.drawable.icon_rock;
            case S:
                return com.chajun.madcamp.R.drawable.icon_scissor;
            case P:
                return com.chajun.madcamp.R.drawable.icon_paper;
            case MR:
                return com.chajun.madcamp.R.drawable.icon_mega_rock;
            case MS:
                return com.chajun.madcamp.R.drawable.icon_mega_scissor;
            case MP:
                return com.chajun.madcamp.R.drawable.icon_mega_paper;
            default:
                return 0;
        }
    }
}
