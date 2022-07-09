package com.chajun.madcamp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.ui.userinfo.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder>{

    private List<User> userList;

    public RankAdapter() {
        userList = new ArrayList<>();
    }

    public void setItems(List<User> items) {
        userList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RankAdapter.RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_rank_list, parent, false);
        return new RankViewHolder(context, view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RankAdapter.RankViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userList.get(position);
        holder.rankingTxt.setText(String.valueOf(holder.getAdapterPosition() + 1));
        holder.nameTxt.setText(user.getName());
        holder.ratingTxt.setText(String.valueOf(user.getRating()));
        holder.winLossesTxt.setText(String.valueOf(user.getWins()) + "승 " + String.valueOf(user.getLosses()) + "패");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra(IntentKey.USER_ID, user.getId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class RankViewHolder extends RecyclerView.ViewHolder {

        TextView rankingTxt;
        TextView nameTxt;
        TextView ratingTxt;
        TextView winLossesTxt;

        RankViewHolder(Context context, View itemView) {
            super(itemView);

            rankingTxt = itemView.findViewById(R.id.list_item_rank_txt_ranking);
            nameTxt = itemView.findViewById(R.id.list_item_rank_txt_name);
            ratingTxt = itemView.findViewById(R.id.list_item_rank_txt_rating);
            winLossesTxt = itemView.findViewById(R.id.list_item_rank_txt_win_and_losses);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}
