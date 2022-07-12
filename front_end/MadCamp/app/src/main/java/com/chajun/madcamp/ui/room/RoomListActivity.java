package com.chajun.madcamp.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chajun.madcamp.R;
import com.chajun.madcamp.adapters.RoomAdapter;
import com.chajun.madcamp.data.model.response.Room;
import com.chajun.madcamp.data.repository.Repository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private RoomAdapter adapter;

    private View backBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room_list);

        initViews();
        setRecyclerView();
        setSwipeRefreshLayout();

        refreshRoomList();


    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshRoomList();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.room_list_swipe_layout);
        recyclerView = findViewById(R.id.room_list_recyclerview);
        backBtn = findViewById(R.id.room_list_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ; // 상하 스크롤r

        adapter = new RoomAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void refreshRoomList() {
        Repository.getInstance().getRoomList(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                try {
                    if (response.isSuccessful()) {
                        adapter.setItems(response.body());
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RoomListActivity.this, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(RoomListActivity.this, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRoomList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
