package com.chajun.madcamp2.data.retrofit

import android.telecom.Call
import com.google.gson.JsonElement
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IRetrofit {
    @FormUrlEncoded
    @POST("/login")
    fun loginRequest(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<JsonElement>
}