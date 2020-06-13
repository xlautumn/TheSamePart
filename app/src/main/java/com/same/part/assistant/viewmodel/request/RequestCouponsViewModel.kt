package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

/**
 * 优惠券管理ViewModel
 */
class RequestCouponsViewModel(application: Application) : BaseViewModel(application) {

    /** 优惠券数据列表*/
    var couponsListResult: MutableLiveData<ArrayList<CouponInfoModel>> = MutableLiveData()

    /**
     * 获取店铺优惠劵活动列表
     */
    fun getCouponsList() {
        requestResponseBody(
            { HttpRequestManger.instance.getCouponsList() },
            { responseBody ->
                val jsonObject = JSON.parseObject(responseBody.string())
                val couponList = ArrayList<CouponInfoModel>()
                jsonObject.getJSONArray("content")?.takeIf { it.size > 0 }?.apply {
                    for (i in 0 until size) {
                        getJSONObject(i)?.apply {
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
                }
                couponsListResult.postValue(couponList)
            },isShowDialog = true
        )
    }




}