package com.chajun.madcamp.data.model.request;

import com.google.gson.annotations.SerializedName;

public class GameCompleteRequest {

    @SerializedName("room_id")
    int roomId;

    @SerializedName("host_id")
    int hostId;

    @SerializedName("guest_id")
    int guestId;

    @SerializedName("game_result")
    int hostGameResult;

    @SerializedName("host_rating_change")
    int hostRatingChange;

    @SerializedName("num_turns")
    int num_turns;

    @SerializedName("game_type")
    String gameType;

    @SerializedName("num_moves")
    int numMoves;

    @SerializedName("host_moves")
    String hostMoves;

    @SerializedName("guest_moves")
    String guestMoves;

    @SerializedName("host_unused_moves")
    String hostUnusedMoves;

    @SerializedName("guest_unused_moves")
    String guestUnusedMoves;

    public GameCompleteRequest(int roomId, int hostId, int guestId, int hostGameResult, int hostRatingChange, int num_turns, String gameType, int numMoves, String hostMoves, String guestMoves, String hostUnusedMoves, String guestUnusedMoves) {
        this.roomId = roomId;
        this.hostId = hostId;
        this.guestId = guestId;
        this.hostGameResult = hostGameResult;
        this.hostRatingChange = hostRatingChange;
        this.num_turns = num_turns;
        this.gameType = gameType;
        this.numMoves = numMoves;
        this.hostMoves = hostMoves;
        this.guestMoves = guestMoves;
        this.hostUnusedMoves = hostUnusedMoves;
        this.guestUnusedMoves = guestUnusedMoves;
    }
}
