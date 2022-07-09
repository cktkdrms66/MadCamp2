package com.chajun.madcamp.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.RankAdapter;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.main.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankListFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RankAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rank_list, container, false);

        initViews(v);
        setRecyclerView();
        setSwipeRefreshLayout();

        refreshRankList();

        return v;
    }

    private void initViews(View v) {
        swipeRefreshLayout = v.findViewById(R.id.rank_list_swipe_layout);
        recyclerView = v.findViewById(R.id.rank_list_recyclerview);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), RecyclerView.VERTICAL, false)) ; // 상하 스크롤r

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
                    Toast.makeText(MainActivity.context, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.context, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
