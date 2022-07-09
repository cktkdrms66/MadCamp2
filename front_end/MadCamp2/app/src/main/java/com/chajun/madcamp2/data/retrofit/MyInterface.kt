package com.chajun.madcamp2.data.retrofit

import com.chajun.madcamp2.data.model.User
import retrofit2.http.GET

interface MyApi {

    @GET("fff")
    suspend fun getPost() : User
}