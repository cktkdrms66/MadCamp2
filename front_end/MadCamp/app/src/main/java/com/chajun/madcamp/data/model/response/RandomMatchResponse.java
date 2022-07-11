package com.chajun.madcamp.data.model.response;

import com.google.gson.annotations.SerializedName;

public class RandomMatchResponse {
    @SerializedName("is_host")
    int isHost;

    @SerializedName("id")
    int id;

    public RandomMatchResponse(int isHost) {
        this.isHost = isHost;
    }

    public int getIsHost() {
        return isHost;
    }

    public void setIsHost(int isHost) {
        this.isHost = isHost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
