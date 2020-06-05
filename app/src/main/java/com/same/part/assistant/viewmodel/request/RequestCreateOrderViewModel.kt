package com.same.part.assistant.viewmodel.request

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.PAYMENT_CATEGORY_FREIGHT_COLLECT
import com.same.part.assistant.data.PAYMENT_CATEGORY_ONLINE_PAY
import com.same.part.assistant.data.PAYMENT_CHANNEL_ALIPAY
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

    var paymentCategory = PAYMENT_CATEGORY_ONLINE_PAY
    var paymentChannel = PAYMENT_CHANNEL_ALIPAY


    /**
     * 获取订单Id
     */
    fun getOrderId() = createOrderResult.value?.let {
        it.content.productOrders[0].productOrderId
    } ?: ""

    /**
     * 是否是货到付款
     */
    fun isFreightCollect() = TextUtils.equals(PAYMENT_CATEGORY_FREIGHT_COLLECT, paymentCategory)

    fun createOrder(cartIds: String) {
        requestResponseBody({
            HttpRequestManger.instance.createOrder(
                "20001",
                cartIds,
                paymentCategory,
                arrayListOf(RequestCreateOrder.OrderRemark(shopId = "2000")),
                CacheUtil.getUserId()?.toString() ?: ""
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

    /**
     * 获取签名的订单信息
     */
    fun getPaySign(
        productOrderId: String
    ) {
        requestResponseBody(
            { HttpRequestManger.instance.getPaySign(productOrderId, paymentChannel) },
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

    fun onPaySuccess() {
        createOrderResult.value = null
        getPaySignResult.value = null
    }
}