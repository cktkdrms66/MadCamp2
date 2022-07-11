package com.chajun.madcamp.data.model.request;

import com.chajun.madcamp.enums.GameType;
import com.google.gson.annotations.SerializedName;

public class NativeLoginRequest {
    String email;
    String password;

    public NativeLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
