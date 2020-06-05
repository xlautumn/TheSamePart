package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.PAYMENT_CHANNEL_ALIPAY
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestTR
import me.hgj.jetpackmvvm.state.ResultState

class RequestPaySignOrderInfoViewModel(application: Application) : BaseViewModel(application) {

    var paymentChannel = PAYMENT_CHANNEL_ALIPAY
    var getPaySignResult = MutableLiveData<ResultState<String>>()

    /**
     * 获取签名的订单信息
     */
    fun getPaySign(
        productOrderId: String
    ) {
        requestTR(
            { HttpRequestManger.instance.getPaySign(productOrderId, paymentChannel) },
            getPaySignResult,
            paresResult = {
                val result = it.string()
                val jsonObject = JSON.parseObject(result)
                val code = jsonObject.getString("code")
                if (code == "1") {
                    val content = jsonObject.getString("content")
                    Triple(content, code, "")
                } else {
                    val msg = jsonObject.getString("message")
                    Triple(null, code, msg)
                }
            },
            isShowDialog = true,
            loadingMessage = "正在支付..."
        )
    }

    fun onPaySuccess() {
        clearData()
    }

    fun clearData() {
        getPaySignResult = MutableLiveData<ResultState<String>>()
    }
}