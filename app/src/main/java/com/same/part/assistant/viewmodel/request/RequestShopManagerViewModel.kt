package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.ShopModel
import com.same.part.assistant.data.model.ShopUserModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultResponseBodyState
import me.hgj.jetpackmvvm.state.ResultState

class RequestShopManagerViewModel(application: Application) : BaseViewModel(application) {

    var shopResult = MutableLiveData<ResultState<ShopModel>>()

    var updateResult = MutableLiveData<ResultResponseBodyState>()

    fun shopModelReq(token: String) {
        request(
            { HttpRequestManger.instance.getShopInfo(token) }
            , shopResult
        )
    }

    fun saveEditContent(img: String, name: String, brand: String) {
        requestResponseBody(
            { HttpRequestManger.instance.updateShopInfo(CacheUtil.getToken(), img, name, brand, CacheUtil.getShopId()?.toString()?:"") },
            updateResult,true,"上传中"
        )
    }

}