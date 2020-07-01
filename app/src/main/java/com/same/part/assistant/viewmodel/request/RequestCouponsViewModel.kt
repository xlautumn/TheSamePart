package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.data.model.CreateOrderResult
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.ext.requestTR
import me.hgj.jetpackmvvm.state.ResultState

/**
 * 优惠券管理ViewModel
 */
class RequestCouponsViewModel(application: Application) : BaseViewModel(application) {

    /** 优惠券数据列表*/
    var couponsListResult: MutableLiveData<ResultState<ArrayList<CouponInfoModel>>> = MutableLiveData()

    /**
     * 获取店铺优惠劵活动列表
     */
    fun getCouponsList() {
        requestTR(
            { HttpRequestManger.instance.getCouponsList() },
            couponsListResult,
            { responseBody ->
                val jsonObject = JSON.parseObject(responseBody.string())
                val couponList = ArrayList<CouponInfoModel>()
                jsonObject.getJSONArray("content")?.takeIf { it.size > 0 }?.let {
                    for (i in 0 until it.size) {
                        it.getJSONObject(i)?.apply {
                            couponList.add(
                                CouponInfoModel(
                                    getString("title"),
                                    getString("totalTake"),
                                    getString("stockQty"),
                                    getString("totalUsed"),
                                    getString("state"),
                                    getString("statements")
                                )
                            )
                        }
                    }
                    Triple(couponList, "", "")
                } ?: kotlin.run {
                    val code = jsonObject.getString("code")
                    val msg = jsonObject.getString("message")
                    Triple(null, code, msg)
                }
            },isShowDialog = true
        )
    }




}