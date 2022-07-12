package com.chajun.madcamp.enums;

//L V
public enum Move {
    R(0), S(1), P(2), L(3), V(4), MR(5), MS(6), MP(7), ML(8), MV(9);

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
            case 3:
                return L;
            case 4:
                return V;
            case 5:
                return MR;
            case 6:
                return MS;
            case 7:
                return MP;
            case 8:
                return ML;
            case 9:
                return MV;
        }

        //TODO
        return Move.MP;
    }

    public static Move getBoostedMove(Move move) {
        return Move.getMove(move.index + 5);
    }

    public int getOriginalIndex() {
        if (this.index >= 5) return this.index - 5;
        else return this.index;
    }



    public static Move getOriginalMove(Move move) {
        return Move.getMove(move.index - 5);
    }


    public int getDrawableId(boolean isExpand) {
        if (isExpand) {
            switch (this) {
                case R:
                    return com.chajun.madcamp.R.drawable.icon_rock_expand;
                case S:
                    return com.chajun.madcamp.R.drawable.icon_scissor_expand;
                case P:
                    return com.chajun.madcamp.R.drawable.icon_paper_expand;
                case L:
                    return com.chajun.madcamp.R.drawable.icon_lizard_expand;
                case V:
                    return com.chajun.madcamp.R.drawable.icon_spock_expand;
                case MR:
                    return com.chajun.madcamp.R.drawable.icon_mega_rock;
                case MS:
                    return com.chajun.madcamp.R.drawable.icon_mega_scissor;
                case MP:
                    return com.chajun.madcamp.R.drawable.icon_mega_paper;
                case ML:
                    return com.chajun.madcamp.R.drawable.icon_mega_lizard;
                case MV:
                    return com.chajun.madcamp.R.drawable.icon_mega_spock;
                default:
                    return 0;
            }
        } else {
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
}
