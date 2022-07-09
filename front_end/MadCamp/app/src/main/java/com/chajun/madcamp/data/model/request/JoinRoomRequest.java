package com.chajun.madcamp.data.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JoinRoomRequest {

    @SerializedName("room_id")
    int roomId;

    @SerializedName("guest_id")
    int guestId;

    @SerializedName("password")
    @Expose
    String password;


    public JoinRoomRequest(int roomId, int guestId, String password) {
        this.roomId = roomId;
        this.guestId = guestId;
        this.password = password;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
