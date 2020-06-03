package com.same.part.assistant.viewmodel.state

import android.app.Application
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


}