package com.chajun.madcamp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chajun.madcamp.R;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.enums.Move;
import com.chajun.madcamp.logic.GameInfo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GameStep1ViewPagerAdapter extends RecyclerView.Adapter<GameStep1ViewPagerAdapter.GameStep1ViewPage>  {
    private List<Move> moveList;

    public GameStep1ViewPagerAdapter(List<Move> data) {
        this.moveList = data;
    }

    @Override
    public GameStep1ViewPage onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.big_item_move, parent, false);
        return new GameStep1ViewPage(view);
    }

    @Override
    public void onBindViewHolder(GameStep1ViewPage holder, int position) {
        GameStep1ViewPage viewHolder = (GameStep1ViewPage) holder;
        viewHolder.onBind(moveList.get(position));
    }

    @Override
    public int getItemCount() {
        return moveList.size();
    }

    public class GameStep1ViewPage extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private Move move;

        GameStep1ViewPage(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.big_item_move_civ_move);
        }

        public void onBind(Move data) {
            move = data;
            imageView.setImageResource(move.getDrawableId(GameInfo.getInstance().gameType == GameType.E));
        }
    }
}
