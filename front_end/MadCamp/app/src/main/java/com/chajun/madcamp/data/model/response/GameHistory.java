package com.chajun.madcamp.data.model.response;


import com.chajun.madcamp.enums.GameType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class GameHistory {
    int id;

    @SerializedName("num_turns")
    int numTurns;

    @SerializedName("num_moves")
    int numMoves;

    @SerializedName("game_type")
    GameType gameType;

    @SerializedName("host_id")
    int hostId;

    @SerializedName("host_name")
    String hostName;

    @SerializedName("guest_id")
    int guestId;

    @SerializedName("guest_name")
    String guestName;

    @SerializedName("host_moves")
    String hostMoves;

    @SerializedName("guest_moves")
    String guestMoves;

    @SerializedName("host_unused_moves")
    String hostUnusedMoves;

    @SerializedName("guest_unused_moves")
    String guestUnusedMoves;

    @SerializedName("date_time")
    Date dateTime;

    public GameHistory(int id, int numTurns, int numMoves, GameType gameType, int hostId, String hostName, int guestId, String guestName, String hostMoves, String guestMoves, String hostUnusedMoves, String guestUnusedMoves, Date dateTime) {
        this.id = id;
        this.numTurns = numTurns;
        this.numMoves = numMoves;
        this.gameType = gameType;
        this.hostId = hostId;
        this.hostName = hostName;
        this.guestId = guestId;
        this.guestName = guestName;
        this.hostMoves = hostMoves;
        this.guestMoves = guestMoves;
        this.hostUnusedMoves = hostUnusedMoves;
        this.guestUnusedMoves = guestUnusedMoves;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getHostMoves() {
        return hostMoves;
    }

    public void setHostMoves(String hostMoves) {
        this.hostMoves = hostMoves;
    }

    public String getGuestMoves() {
        return guestMoves;
    }

    public void setGuestMoves(String guestMoves) {
        this.guestMoves = guestMoves;
    }

    public String getHostUnusedMoves() {
        return hostUnusedMoves;
    }

    public void setHostUnusedMoves(String hostUnusedMoves) {
        this.hostUnusedMoves = hostUnusedMoves;
    }

    public String getGuestUnusedMoves() {
        return guestUnusedMoves;
    }

    public void setGuestUnusedMoves(String guestUnusedMoves) {
        this.guestUnusedMoves = guestUnusedMoves;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}


