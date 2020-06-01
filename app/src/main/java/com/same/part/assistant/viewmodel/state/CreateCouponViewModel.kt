package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.IntLiveData
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

/**
 * 添加优惠券
 */
class CreateCouponViewModel(application: Application) : BaseViewModel(application) {

    /** 名称 */
    var name = StringLiveData("")

    /** 发放总量 */
    var distributionTotal = StringLiveData("")

    /** 减免金额 */
    var creditAmount = StringLiveData("")

    /** 使用门槛 */
    var usingThreshold = StringLiveData("")

    /** 用券时间 */
    var useCouponTime = StringLiveData("")

    /** 优惠券领取渠道 */
    var couponReceiveChannel = StringLiveData("")

}