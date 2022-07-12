package com.chajun.madcamp.ui.userinfo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.GameHistoryAdapter;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.data.model.response.GameHistory;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.util.Util;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    private TextView nameTxt;
    private TextView rankingTxt;
    private TextView ratingTxt;
    private TextView winsLossesTxt;

    private RecyclerView recyclerView;
    private GameHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View backBtn;


    private int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userId = getIntent().getIntExtra(IntentKey.USER_ID, -1);

        if (userId == -1) {
            Util.makeDialog(this, R.string.user_info_error, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        } else {
            initViews();
            setUserInfoTxts();
            setRecyclerView();
            refreshGameHistoryList();
            setSwipeRefreshLayout();

        }
    }

    private void initViews() {
        nameTxt = findViewById(R.id.user_info_txt_name);
        rankingTxt = findViewById(R.id.user_info_txt_ranking);
        ratingTxt = findViewById(R.id.user_info_txt_rating);
        winsLossesTxt = findViewById(R.id.user_info_txt_win_and_losses);

        recyclerView = findViewById(R.id.user_info_recyclerview);
        swipeRefreshLayout = findViewById(R.id.user_info_swipe_layout);

        backBtn = findViewById(R.id.user_info_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUserInfoTxts() {
        Repository.getInstance().getUserInfo(userId, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    nameTxt.setText(user.getName());
                    ratingTxt.setText(user.getRating());
                    rankingTxt.setText(user.getRank());
                    winsLossesTxt.setText(user.getWins() + "승 " + user.getLosses() + "패");
                } else {
                    Util.makeDialog(UserInfoActivity.this, R.string.user_info_error, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Util.makeDialog(UserInfoActivity.this, R.string.user_info_error, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
            }
        });
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGameHistoryList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ; // 상하 스크롤r

        adapter = new GameHistoryAdapter(userId, this);
        recyclerView.setAdapter(adapter);
    }

    private void refreshGameHistoryList() {
        Toast errorToast = Toast.makeText(this, R.string.list_refresh_error, Toast.LENGTH_SHORT);

        Repository.getInstance().getGameHistoryList(userId, new Callback<List<GameHistory>>() {
            @Override
            public void onResponse(Call<List<GameHistory>> call, Response<List<GameHistory>> response) {
                try {
                    if (response.isSuccessful()) {
                        adapter.setItems(response.body());;
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
}
