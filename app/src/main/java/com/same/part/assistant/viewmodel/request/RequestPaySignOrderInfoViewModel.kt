package com.same.part.assistant.viewmodel.request

import android.app.Application
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.PAYMENT_CHANNEL_ALIPAY
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestPaySignOrderInfoViewModel(application: Application) : BaseViewModel(application) {

    var paymentChannel = PAYMENT_CHANNEL_ALIPAY
    val getPaySignResult = StringLiveData()

    /**
     * 获取签名的订单信息
     */
    fun getPaySign(
        productOrderId: String
    ) {
        requestResponseBody(
            { HttpRequestManger.instance.getPaySign(productOrderId, paymentChannel) },
            success = {
                val result = it.string()
                val jsonObject = JSON.parseObject(result)
                val code = jsonObject.getString("code")
                if (code == "1") {
                    val content = jsonObject.getString("content")
                    getPaySignResult.postValue(content)
                } else {
                    val msg = jsonObject.getString("message")
                    ToastUtils.showShort(msg)
                }
            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            })
    }

    fun onPaySuccess() {
        clearData()
    }

    fun clearData(){
        getPaySignResult.value = ""
    }
}