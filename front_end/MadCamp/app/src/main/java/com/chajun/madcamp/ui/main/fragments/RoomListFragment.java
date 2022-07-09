package com.chajun.madcamp.ui.main.fragments;

import android.content.Intent;
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
import com.chajun.madcamp.adapters.RoomAdapter;
import com.chajun.madcamp.data.model.response.Room;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.main.AddRoomActivity;
import com.chajun.madcamp.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomListFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private RoomAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_room_list, container, false);

        initViews(v);
        setRecyclerView();
        setAddButton();
        setSwipeRefreshLayout();

        refreshRoomList();

        return v;
    }

    private void initViews(View v) {
        swipeRefreshLayout = v.findViewById(R.id.room_list_swipe_layout);
        recyclerView = v.findViewById(R.id.room_list_recyclerview);
        fab = v.findViewById(R.id.room_list_fab_add);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity(), RecyclerView.VERTICAL, false)) ; // 상하 스크롤r

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
                    Toast.makeText(MainActivity.context, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(MainActivity.context, R.string.list_refresh_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setAddButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.context, AddRoomActivity.class));
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
