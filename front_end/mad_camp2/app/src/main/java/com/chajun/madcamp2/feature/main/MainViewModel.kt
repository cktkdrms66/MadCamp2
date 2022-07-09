package com.chajun.madcamp2.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chajun.madcamp2.data.model.MyModel
import com.chajun.madcamp2.data.repository.MyRepository

class MainViewModel(private val repository: MyRepository) : ViewModel() {
    private val _myModels: MutableLiveData<List<MyModel>> = MutableLiveData(emptyList())
    val myModels: LiveData<List<MyModel>> get() = _myModels

    val onItemClickEvent: MutableLiveData<MyModel> = MutableLiveData()


    private val _text: MutableLiveData<String> = MutableLiveData("qwe")
    val text: LiveData<String> get() = _text

    fun loadMyModels() {
        repository.getModels().let {
            print(it)
            _myModels.postValue(it)
        }
    }


    fun onItemClick(position: Int) {
        _myModels.value?.getOrNull(position)?.let {
            onItemClickEvent.postValue(it)
        }

        _text.postValue("fwefewfwfwfwfwef")
    }

}