package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.CashierDetailMode
import com.same.part.assistant.data.model.CreateOrderResult
import com.same.part.assistant.data.model.GetPaySignResult
import com.same.part.assistant.data.model.RequestCreateOrder
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultState

class RequestCreateOrderViewModel(application: Application) : BaseViewModel(application) {
    val createOrderResult = MutableLiveData<CreateOrderResult>()

    val getPaySignResult = MutableLiveData<GetPaySignResult>()

    fun createOrder(cartIds: String) {
        requestResponseBody({
            HttpRequestManger.instance.createOrder(
                "20001",
                cartIds,
                "在线付款",
                arrayListOf<RequestCreateOrder.OrderRemark>(),
                "21272"
            )
        }, success = {
            val result = GsonUtils.fromJson(it.string(), CreateOrderResult::class.java)
            if (result.code == 1) {
                createOrderResult.postValue(result)
            } else {
                ToastUtils.showShort(result.msg)
            }
        }, error = {
            ToastUtils.showShort(it.errorMsg)
        })
    }

    fun getPaySign(
        productOrderId: String,
        payment: String
    ) {
        requestResponseBody(
            { HttpRequestManger.instance.getPaySign(productOrderId, payment) },
            success = {
                val result = GsonUtils.fromJson(it.string(), GetPaySignResult::class.java)
                if (result.code == 1) {
                    getPaySignResult.postValue(result)
                } else {
                    ToastUtils.showShort(result.msg)
                }
            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            })
    }
}