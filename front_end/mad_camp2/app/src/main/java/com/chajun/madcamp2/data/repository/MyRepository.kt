package com.chajun.madcamp2.data.repository

import com.chajun.madcamp2.data.model.MyModel


interface MyRepository {
    fun getModels(): List<MyModel>
}
class MyRepositoryImpl: MyRepository {

    val list: ArrayList<MyModel> = ArrayList()
    override fun getModels(): List<MyModel> {
        list.add(MyModel("qwe", "qwe"))
        list.add(MyModel("qwe", "qwe"))
        list.add(MyModel("qwe", "qwe"))
        val qwe = ArrayList<MyModel>()
        qwe.addAll(list)
        return qwe
    }
}