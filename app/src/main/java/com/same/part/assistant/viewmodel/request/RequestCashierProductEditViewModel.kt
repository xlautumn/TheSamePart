package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.GetCashierProductMsg
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.fragment.SelectSuitableProductFragment
import com.same.part.assistant.viewmodel.state.SearchBaseViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultState
import okhttp3.ResponseBody

/**
 * 搜索收银商品(编辑商品)
 */
class RequestCashierProductEditViewModel(application: Application) :
    SearchBaseViewModel<CashierProduct>(application) {

    val cashierProductResultState = MutableLiveData<ResultState<GetCashierProductMsg>>()


    /**
     * 请求搜索结果
     */
    override fun requestSearchResultList(name: String) {
        getCashierProductList(name)
    }

    override val KEY_HISTORY_DATA: String = "KEY_CASHIER_PRODUCT_EDIT_HISTORY_DATA"

    /**
     * 获取收银商品列表
     */
    private fun getCashierProductList(name: String) {
        request(
            { HttpRequestManger.instance.getCashierProductList(name, 0, 50) },
            cashierProductResultState, true, "正在搜索..."
        )
    }

    /**
     * 更新商品上下架状态
     */
    fun updateProduct(
        cashierProduct: CashierProduct,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        fun parseResponseBody(
            it: ResponseBody,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            val response: String = it.string()
            val responseObject = JSON.parseObject(response)
            val code = responseObject.getInteger("code")
            val msg = responseObject.getString("message")
            if (code == 1) {
                onSuccess(msg)
            } else {
                onError(msg)
            }
        }
        if (cashierProduct.state == "1") {
            requestResponseBody({ HttpRequestManger.instance.undercarriageProduct(cashierProduct.productId) },
                success = {
                    parseResponseBody(it, onSuccess, onError)
                }, error = {
                    onError(it.errorMsg)
                })
        } else {
            requestResponseBody({ HttpRequestManger.instance.putawayProduct(cashierProduct.productId) },
                success = {
                    parseResponseBody(it, onSuccess, onError)
                }, error = {
                    onError(it.errorMsg)
                })
        }
    }

    /**
     * 批量删除收银商品
     */
    fun delCashierProduct(cashierProduct: CashierProduct,
                          onSuccess: (String) -> Unit,
                          onError: (String) -> Unit){
        fun parseResponseBody(
            it: ResponseBody,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            val response: String = it.string()
            val responseObject = JSON.parseObject(response)
            val code = responseObject.getInteger("code")
            val msg = responseObject.getString("message")
            if (code == 1) {
                searchResultList.value?.let {
                    it.remove(cashierProduct)
                    setSearchResultList(it)
                }
                onSuccess(msg)
            } else {
                onError(msg)
            }
        }
        requestResponseBody({ HttpRequestManger.instance.delCashierProduct(arrayListOf(cashierProduct.productId)) },
            success = {
                parseResponseBody(it, onSuccess, onError)
            }, error = {
                onError(it.errorMsg)
            })
    }

}