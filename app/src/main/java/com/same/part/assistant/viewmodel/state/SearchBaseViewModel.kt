package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.utils.SharedPreferenceUtil
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData


abstract class SearchBaseViewModel<E>(application: Application) : BaseViewModel(application) {

    abstract val KEY_HISTORY_DATA: String

    private val _searchResultList = MutableLiveData<ArrayList<E>>()
    val searchResultList: LiveData<ArrayList<E>> = _searchResultList

    val showResult = BooleanLiveData(false)

    var hasSearchResult = BooleanLiveData(false)

    var showHistory = BooleanLiveData(false)

    var showClear = BooleanLiveData(false)

    private val _searchHistoryData = MutableLiveData<ArrayList<String>>()

    val searchHistoryData: LiveData<ArrayList<String>> = _searchHistoryData

    init {
        _searchHistoryData.value = arrayListOf()
        _searchResultList.value = arrayListOf()
    }

    fun getHistoryData(sp: SharedPreferenceUtil) {
        val histories = arrayListOf<String>()
        val history = sp.getString(KEY_HISTORY_DATA)
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
                sp.removeByKey(KEY_HISTORY_DATA)
            } else {
                val builder = StringBuilder()
                histories.forEachIndexed { index, history ->
                    builder.append(history)
                    histories.takeIf { index != histories.lastIndex }?.apply {
                        builder.append(",")
                    }
                }
                sp.saveString(KEY_HISTORY_DATA, builder.toString())
            }
        }
    }

    fun clearHistory() {
        _searchHistoryData.value = arrayListOf()
        _searchHistoryData.postValue(_searchHistoryData.value)
    }

    fun setSearchResultList(data: List<E>) {
        _searchResultList.value?.apply {
            if (data != this) {
                clear()
                addAll(data)
            }
        }
        _searchResultList.value = _searchResultList.value
    }

    /**
     * 请求搜索结果
     */
    abstract fun requestSearchResultList(name: String)

    fun clear() {
        _searchResultList.value = arrayListOf()
    }

}