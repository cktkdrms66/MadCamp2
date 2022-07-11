package com.chajun.madcamp.data.retrofit;

import com.chajun.madcamp.data.model.request.AddRoomRequest;
import com.chajun.madcamp.data.model.request.GameCompleteRequest;
import com.chajun.madcamp.data.model.request.JoinRoomRequest;
import com.chajun.madcamp.data.model.request.KakaoLoginRequest;
import com.chajun.madcamp.data.model.request.NativeLoginRequest;
import com.chajun.madcamp.data.model.request.NativeRegisterRequest;
import com.chajun.madcamp.data.model.request.RandomMatchRequest;
import com.chajun.madcamp.data.model.response.AddRoomResponse;
import com.chajun.madcamp.data.model.response.GameCompleteResponse;
import com.chajun.madcamp.data.model.response.GameHistory;
import com.chajun.madcamp.data.model.response.JoinRoomResponse;
import com.chajun.madcamp.data.model.response.KakaoLoginResponse;
import com.chajun.madcamp.data.model.response.NativeLoginResponse;
import com.chajun.madcamp.data.model.response.NativeRegisterResponse;
import com.chajun.madcamp.data.model.response.RandomMatchResponse;
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

    @POST("game/complete")
    Call<GameCompleteResponse> completeGame(@Body GameCompleteRequest request);

    @POST("user/login/native")
    Call<NativeLoginResponse> loginNative(@Body NativeLoginRequest request);

    @POST("user/register/native")
    Call<NativeRegisterResponse> register(@Body NativeRegisterRequest request);

    @POST("user/login/kakao")
    Call<KakaoLoginResponse> loginKakao(@Body KakaoLoginRequest request);

    @POST("room/random")
    Call<RandomMatchResponse> randomMatch(@Body RandomMatchRequest request);

}
