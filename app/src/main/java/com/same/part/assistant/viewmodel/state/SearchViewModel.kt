package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.utils.SharedPreferenceUtil
import com.same.part.assistant.utils.SharedPreferenceUtil.Companion.SEARCH_HISTORY
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData


class SearchViewModel(application: Application) : BaseViewModel(application) {

    val showResult = BooleanLiveData(false)

    var hasSearchResult = BooleanLiveData(false)

    var showHistory = BooleanLiveData(false)

    var showClear = BooleanLiveData(false)

    private val _searchHistoryData = MutableLiveData<ArrayList<String>>()

    val searchHistoryData: LiveData<ArrayList<String>> = _searchHistoryData
    init {
        _searchHistoryData.value = arrayListOf()
    }

    fun getHistoryData(sp: SharedPreferenceUtil) {
        val histories = arrayListOf<String>()
        val history = sp.getString(SEARCH_HISTORY)
        history.takeIf { it.isNotEmpty() }?.let {
            val splits = it.split(",")
            splits.forEach { split ->
                histories.add(split)
            }
        }
        _searchHistoryData.value?.addAll(histories)
        _searchHistoryData.postValue(_searchHistoryData.value)
    }

    fun refreshHistoryData(search: String) {
        _searchHistoryData.value?.takeIf { !it.contains(search) }?.let {
            it.add(search)
            _searchHistoryData.postValue(_searchHistoryData.value)
        }

    }

    fun updateHistoryCache(sp: SharedPreferenceUtil) {
        _searchHistoryData.value?.let { histories ->
            if (histories.isEmpty()) {
                sp.removeByKey(SEARCH_HISTORY)
            } else {
                val builder = StringBuilder()
                histories.forEachIndexed { index, history ->
                    builder.append(history)
                    histories.takeIf { index != histories.lastIndex }?.apply {
                        builder.append(",")
                    }
                }
                sp.saveString(SEARCH_HISTORY, builder.toString())
            }
        }
    }

    fun clearHistory(){
        _searchHistoryData.value= arrayListOf()
        _searchHistoryData.postValue(_searchHistoryData.value)
    }

}