package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.RequestCreateCouponInfo
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultResponseBodyState

/**
 * 添加优惠券请求ViewModel
 */
class RequestCreateCouponViewModel(application: Application) : BaseViewModel(application) {

    var saveResult = MutableLiveData<ResultResponseBodyState>()

    /**
     * 添加优惠券
     */
    fun createCouponActivity(requestCreateCouponInfo: RequestCreateCouponInfo) {
        requestResponseBody(
            { HttpRequestManger.instance.createCouponActivity(requestCreateCouponInfo) }
            , saveResult,true,"添加中..."
        )
    }
}