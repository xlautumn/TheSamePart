package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.same.part.assistant.data.model.PurchaseOrderModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestTR
import me.hgj.jetpackmvvm.state.ResultState

class RequestConformDeliveryViewModel(application: Application) : BaseViewModel(application) {

    var conformDeliveryResult = MutableLiveData<ResultState<PurchaseOrderModel>>()

    /**
     * 确认收货
     */
    fun conformDelivery(orderId: String) {
        requestTR({
            HttpRequestManger.instance.conformDelivery(orderId)
        },
            conformDeliveryResult,
            paresResult = {
                val result = it.string()
                val jsonObject = JSON.parseObject(result)
                val code = jsonObject.getString("code")
                if (code == "1") {
                    val content = jsonObject.getString("content")
                    Triple(GsonUtils.fromJson(content, PurchaseOrderModel::class.java), code, "")
                } else {
                    val msg = jsonObject.getString("message")
                    Triple(null, code, msg)
                }
            })
    }

}