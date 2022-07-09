package com.chajun.madcamp2.data.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val BASE_URL = "https://dff5a8b7-2912-475d-a123-23bfec5f420f.mock.pstmn.io"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api : MyApi by lazy {
        retrofit.create(MyApi::class.java)
    }
}