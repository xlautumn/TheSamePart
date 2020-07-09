package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.GetCashierProductMsg
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.fragment.SelectSuitableProductFragment
import com.same.part.assistant.viewmodel.state.SearchBaseViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

/**
 * 搜索收银商品(添加适用商品)
 */
class RequestCashierProductViewModel(application: Application) :
    SearchBaseViewModel<CashierProduct>(application) {

    val cashierProductResultState = MutableLiveData<ResultState<GetCashierProductMsg>>()

    /**
     * 请求搜索结果
     */
    override fun requestSearchResultList(name: String) {
        getCashierProductList(name)
    }

    override val KEY_HISTORY_DATA: String = "KEY_CASHIER_PRODUCT_HISTORY_DATA"

    /**
     * 获取收银商品列表
     */
    private fun getCashierProductList(name: String) {
        request(
            { HttpRequestManger.instance.getCashierProductList(name, 0, 50,1) },
            cashierProductResultState, true, "正在搜索..."
        )
    }

}