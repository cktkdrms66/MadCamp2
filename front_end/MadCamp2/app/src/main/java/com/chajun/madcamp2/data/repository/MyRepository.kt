package com.chajun.madcamp2.data.repository

import com.chajun.madcamp2.data.model.MyModel
import com.chajun.madcamp2.data.model.User
import com.chajun.madcamp2.data.retrofit.RetrofitInstance


class Repository {

    suspend fun getPost() : User {
        return RetrofitInstance.api.getPost()
    }
}