package com.chajun.madcamp.ui.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.RankAdapter;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RankAdapter adapter;

    private View backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rank_list);

        initViews();
        setRecyclerView();
        setSwipeRefreshLayout();

        refreshRankList();

    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.rank_list_swipe_layout);
        recyclerView = findViewById(R.id.rank_list_recyclerview);
        backBtn = findViewById(R.id.rank_list_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ; // 상하 스크롤r

        adapter = new RankAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRankList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshRankList() {
        Repository.getInstance().getRankingList(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<User> userList = response.body();
                        adapter.setItems(userList);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RankListActivity.this, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(RankListActivity.this, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
