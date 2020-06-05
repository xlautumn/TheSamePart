package com.same.part.assistant.viewmodel.request

import android.app.Application
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.model.ProductClassificationModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestCustomerViewModel (application: Application) : BaseViewModel(application){

    /**
     * 获取店铺客户列表
     */
    fun getCustomerList() {
        //查商品分类
        requestResponseBody(
            { HttpRequestManger.instance.getCustomerList() },
            success = { responsebody ->
            }, isShowDialog = true
        )
    }
}