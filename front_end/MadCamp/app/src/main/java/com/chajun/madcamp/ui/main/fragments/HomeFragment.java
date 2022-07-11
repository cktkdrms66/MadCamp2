package com.chajun.madcamp.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.Constant;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.config.SharedPrefKey;
import com.chajun.madcamp.config.SocketMsg;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.request.RandomMatchRequest;
import com.chajun.madcamp.data.model.response.RandomMatchResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.ui.game.GameActivity;
import com.chajun.madcamp.ui.login.LoginActivity;
import com.chajun.madcamp.ui.main.AddRoomActivity;
import com.chajun.madcamp.ui.main.MainActivity;
import com.chajun.madcamp.ui.userinfo.UserInfoActivity;
import com.chajun.madcamp.util.Util;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    private Button randomMatchStartBtn;
    private Button myInfoBtn;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(v);
        setMyInfoBtn();

        randomMatchStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Util.setString(MainActivity.context, SharedPrefKey.EMAIL, "");
//                Util.setString(MainActivity.context, SharedPrefKey.PASSWORD, "");
//                startActivity(new Intent(MainActivity.context, LoginActivity.class));
//                ((MainActivity) MainActivity.context).finish();

                Repository.getInstance().randomMatch(new RandomMatchRequest(AppData.userId), new Callback<RandomMatchResponse>() {
                    @Override
                    public void onResponse(Call<RandomMatchResponse> call, Response<RandomMatchResponse> response) {
                        if (response.isSuccessful()) {

                            boolean isHost = response.body().getIsHost() == 1;
                            int roomId = response.body().getId();

                            System.out.println("is Host : " + isHost);
                            System.out.println("roomId : " + roomId);


                            Intent intent = new Intent(MainActivity.context, GameActivity.class);
                            intent.putExtra(IntentKey.ROOM_ID, roomId);
                            intent.putExtra(IntentKey.IS_EXPANDED, Constant.DEFAULT_GAME_TYPE == GameType.E);
                            intent.putExtra(IntentKey.NUM_TURNS, Constant.DEFAULT_NUM_TURN);
                            intent.putExtra(IntentKey.NUM_MOVES, Constant.DEFAULT_NUM_DECK);
                            intent.putExtra(IntentKey.IS_HOST, isHost);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<RandomMatchResponse> call, Throwable t) {
                        System.out.println("fail!!!!");
                    }
                });
            }
        });

        return v;
    }

    private void initViews(View v) {
        randomMatchStartBtn = v.findViewById(R.id.home_btn_random_match_start);
        myInfoBtn = v.findViewById(R.id.home_btn_my_info);


    }

    private void setMyInfoBtn() {
        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeFragment.this.getActivity(), UserInfoActivity.class);
                intent.putExtra(IntentKey.USER_ID, AppData.userId);
                startActivity(intent);
            }
        });
    }
}
