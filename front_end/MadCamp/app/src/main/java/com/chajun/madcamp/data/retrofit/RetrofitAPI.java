package com.chajun.madcamp.data.retrofit;

import com.chajun.madcamp.data.model.request.AddRoomRequest;
import com.chajun.madcamp.data.model.request.JoinRoomRequest;
import com.chajun.madcamp.data.model.response.AddRoomResponse;
import com.chajun.madcamp.data.model.response.GameHistory;
import com.chajun.madcamp.data.model.response.JoinRoomResponse;
import com.chajun.madcamp.data.model.response.Room;
import com.chajun.madcamp.data.model.response.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("room/list")
    Call<List<Room>> getRoomList();

    @POST("room/add")
    Call<AddRoomResponse> addRoom(@Body AddRoomRequest request);

    @POST("room/join")
    Call<JoinRoomResponse> joinRoom(@Body JoinRoomRequest request);

    @GET("user/leaderboard")
    Call<List<User>> getRankingList();

    @GET("user/info")
    Call<User> getUserInfo(@Query("id") int userId);

    @GET("record/list")
    Call<List<GameHistory>> getGameHistoryList(@Query("id") int userId);

}
