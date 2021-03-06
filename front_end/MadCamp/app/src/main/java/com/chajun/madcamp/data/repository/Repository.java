package com.chajun.madcamp.data.repository;

import com.chajun.madcamp.data.model.request.GameCompleteRequest;
import com.chajun.madcamp.data.model.request.JoinRoomRequest;
import com.chajun.madcamp.data.model.request.KakaoLoginRequest;
import com.chajun.madcamp.data.model.request.NativeLoginRequest;
import com.chajun.madcamp.data.model.request.NativeRegisterRequest;
import com.chajun.madcamp.data.model.request.RandomMatchRequest;
import com.chajun.madcamp.data.model.response.GameCompleteResponse;
import com.chajun.madcamp.data.model.response.JoinRoomResponse;
import com.chajun.madcamp.data.model.response.KakaoLoginResponse;
import com.chajun.madcamp.data.model.response.NativeLoginResponse;
import com.chajun.madcamp.data.model.response.NativeRegisterResponse;
import com.chajun.madcamp.data.model.response.RandomMatchResponse;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.data.model.request.AddRoomRequest;
import com.chajun.madcamp.data.model.response.AddRoomResponse;
import com.chajun.madcamp.data.model.response.GameHistory;
import com.chajun.madcamp.data.model.response.Room;
import com.chajun.madcamp.data.model.response.User;
import com.chajun.madcamp.data.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class Repository {

    public static Repository getInstance() {
        return new Repository();
    }

    public void getRoomList(Callback<List<Room>> callback) {
        Call<List<Room>> call = RetrofitClient.getApiService().getRoomList();
        call.enqueue(callback);
    }

    public void addRoom(AddRoomRequest request, Callback<AddRoomResponse> callback) {
        Call<AddRoomResponse> call = RetrofitClient.getApiService().addRoom(request);
        call.enqueue(callback);
    }

    public void joinRoom(JoinRoomRequest request, Callback<JoinRoomResponse> callback) {
        Call<JoinRoomResponse> call = RetrofitClient.getApiService().joinRoom(request);
        call.enqueue(callback);
    }

    public void getRankingList(Callback<List<User>> callback) {
        Call<List<User>> call = RetrofitClient.getApiService().getRankingList();
        call.enqueue(callback);
    }

    public void getUserInfo(int userId, Callback<User> callback) {
        Call<User> call = RetrofitClient.getApiService().getUserInfo(userId);
        call.enqueue(callback);
    }


    public void getGameHistoryList(int userId, Callback<List<GameHistory>> callback) {
        Call<List<GameHistory>> call = RetrofitClient.getApiService().getGameHistoryList(userId);
        call.enqueue(callback);
    }

    public void completeGame(GameCompleteRequest request, Callback<GameCompleteResponse> callback) {
        Call<GameCompleteResponse> call = RetrofitClient.getApiService().completeGame(request);
        call.enqueue(callback);
    }

    public void loginNative(NativeLoginRequest request, Callback<NativeLoginResponse> callback) {
        Call<NativeLoginResponse> call = RetrofitClient.getApiService().loginNative(request);
        call.enqueue(callback);
    }

    public void registerNative(NativeRegisterRequest request, Callback<NativeRegisterResponse> callback) {
        Call<NativeRegisterResponse> call = RetrofitClient.getApiService().register(request);
        call.enqueue(callback);
    }

    public void loginKakao(KakaoLoginRequest request, Callback<KakaoLoginResponse> callback) {
        Call<KakaoLoginResponse> call = RetrofitClient.getApiService().loginKakao(request);
        call.enqueue(callback);
    }

    public void randomMatch(RandomMatchRequest request, Callback<RandomMatchResponse> callback) {
        Call<RandomMatchResponse> call = RetrofitClient.getApiService().randomMatch(request);
        call.enqueue(callback);
    }
}
