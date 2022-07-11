package com.chajun.madcamp.data.model.response;

import com.chajun.madcamp.enums.GameState;
import com.google.gson.annotations.SerializedName;

public class Room {
    int id;

    @SerializedName("room_number")
    int roomNumber;
    String title;

    @SerializedName("num_moves")
    int numMoves;

    @SerializedName("num_turns")
    int numTurns;

    @SerializedName("game_type")
    String gameType;

    @SerializedName("host_name")
    String hostName;
    GameState state;
    String password;

    int locked;
    int hidden;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(int numTurns) {
        this.numTurns = numTurns;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return locked == 1;
    }

    public void setLocked(int locked) {
        this.locked = locked;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public int getLocked() {
        return locked;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }
}
