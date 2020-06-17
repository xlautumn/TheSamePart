package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData


class SearchViewModel(application: Application) : BaseViewModel(application) {

    val showResult = BooleanLiveData(false)

    var hasSearchResult = BooleanLiveData(false)

    var showHistory = BooleanLiveData(false)

    var showClear = BooleanLiveData(false)

    private val _searchHistoryData = MutableLiveData<List<String>>()

    val searchHistoryData: LiveData<List<String>> = _searchHistoryData

    init {
        _searchHistoryData.value = getHistoryData()
    }

    private fun getHistoryData(): List<String> {
        return arrayListOf<String>()
    }

}