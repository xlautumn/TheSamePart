package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.viewmodel.state.SearchBaseViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData

/**
 * 搜索客户
 */
class RequestSearchCustomViewModel(application: Application) : SearchBaseViewModel<ProductDetailData>(application) {
    /**
     * 请求搜索结果
     */
    override fun requestSearchResultList(name: String) {

    }

    override val KEY_HISTORY_DATA: String = "KEY_CUSTOM_HISTORY_DATA"

}