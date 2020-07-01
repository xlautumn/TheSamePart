package com.same.part.assistant.activity

import android.os.Bundle
import com.same.part.assistant.R
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.viewmodel.state.ProvideCouponViewModel
import me.hgj.jetpackmvvm.base.activity.BaseVmActivity

class ProvideCouponActivity : BaseVmActivity<ProvideCouponViewModel>() {

    override fun layoutId(): Int = R.layout.activity_provide_coupon

    override fun initView(savedInstanceState: Bundle?) {
        mViewModel.couponInfoModel = intent.getSerializableExtra(KEY_DATA_COUPON) as? CouponInfoModel
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

    override fun createObserver() {

    }

    companion object {
        const val KEY_DATA_COUPON = "KEY_DATA_COUPON"
    }
}