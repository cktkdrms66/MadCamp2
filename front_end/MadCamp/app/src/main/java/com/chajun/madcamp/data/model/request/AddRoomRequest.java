package com.chajun.madcamp.data.model.request;

import com.chajun.madcamp.enums.GameType;
import com.google.gson.annotations.SerializedName;

public class AddRoomRequest {
    String title;
    @SerializedName("game_type")
    GameType gameType;
    @SerializedName("num_turns")
    int numTurns;
    @SerializedName("num_moves")
    int numMoves;
    @SerializedName("host_id")
    int hostId;

    int locked;
    String password;

    public AddRoomRequest(String title, GameType gameType, int numTurns, int numMoves, int hostId, int locked, String password) {
        this.title = title;
        this.gameType = gameType;
        this.numTurns = numTurns;
        this.numMoves = numMoves;
        this.hostId = hostId;
        this.locked = locked;
        this.password = password;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(int numTurns) {
        this.numTurns = numTurns;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getLocked() {
        return locked;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
