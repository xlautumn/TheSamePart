package com.same.part.assistant.viewmodel.state

import android.app.Application
import com.same.part.assistant.data.model.CouponInfoModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class ProvideCouponViewModel(application: Application) : BaseViewModel(application) {
    /**
     * 优惠券对象
     */
    var couponInfoModel: CouponInfoModel? = null
}