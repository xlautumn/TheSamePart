package com.same.part.assistant.viewmodel.state

import android.app.Application
import com.same.part.assistant.data.model.CashierProduct
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.IntObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.callback.livedata.IntLiveData
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

/**
 * 添加优惠券
 */
class CreateCouponViewModel(application: Application) : BaseViewModel(application) {

    /** 名称 */
    var name = StringObservableField()

    /** 发放总量 */
    var distributionTotal = StringObservableField()

    /** 减免金额 */
    var creditAmount = StringObservableField()

    /** 使用门槛 */
    var usingThreshold =StringObservableField("0")

    /** 优惠券领取渠道 */
    var couponReceiveChannel = IntObservableField(1)

    /** 开始时间 */
    var startTime = StringObservableField()

    /** 结束 */
    var endTime = StringObservableField()

    /**使用范围类型。partIn：部分商品可用;partOut：部分商品不可用；all：全部商品可用(默认all)*/
    val rangeType = StringObservableField(RANGE_TYPE_ALL)

    /**使用范围值(商品JSON数组)(range_type=part时启用) [1,2,3,商品ID]*/
    var rangeValue:ArrayList<CashierProduct>? = null

    companion object{
        const val RANGE_TYPE_ALL = "all"//全部商品
        const val RANGE_TYPE_PART_IN = "partIn"//部分商品可用
    }

}