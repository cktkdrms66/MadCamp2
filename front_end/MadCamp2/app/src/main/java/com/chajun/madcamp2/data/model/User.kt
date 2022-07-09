package com.chajun.madcamp2.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null
)
