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
import com.same.part.assistant.data.model.CreateOrderResult
import com.same.part.assistant.data.model.RequestCreateOrder
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestCreateOrderViewModel(application: Application) : BaseViewModel(application) {
    val createOrderResult = MutableLiveData<CreateOrderResult>()

    var paymentCategory = PAYMENT_CATEGORY_ONLINE_PAY



    /**
     * 获取订单Id
     */
    fun getOrderId() = createOrderResult.value?.let {
        it.productOrders[0].productOrderId
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
            val result = it.string()
            val jsonObject = JSON.parseObject(result)
            val code = jsonObject.getString("code")
            if (code == "1") {
                val content = jsonObject.getString("content")
                val result = GsonUtils.fromJson(content, CreateOrderResult::class.java)
                createOrderResult.postValue(result)
            } else {
                val msg = jsonObject.getString("message")
                ToastUtils.showShort(msg)
            }

        }, error = {
            ToastUtils.showShort(it.errorMsg)
        },
            isShowDialog = true,
            loadingMessage = "正在生成订单"
        )
    }



    fun onPaySuccess() {
        clearData()
    }

    fun clearData(){
        createOrderResult.value = null
    }
}