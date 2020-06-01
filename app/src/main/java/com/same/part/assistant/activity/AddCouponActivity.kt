package com.same.part.assistant.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.setCanInput
import com.same.part.assistant.data.model.RequestCreateCouponInfo
import com.same.part.assistant.databinding.ActivityAddCouponBinding
import com.same.part.assistant.databinding.ActivityCouponManagerBinding
import com.same.part.assistant.viewmodel.request.RequestCouponsViewModel
import com.same.part.assistant.viewmodel.request.RequestCreateCouponViewModel
import com.same.part.assistant.viewmodel.request.RequestUploadDataViewModel
import com.same.part.assistant.viewmodel.state.CreateCouponViewModel
import kotlinx.android.synthetic.main.activity_shop_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseStateResponseBody

/**
 * 添加会员卡
 */
class AddCouponActivity :
    BaseActivity<CreateCouponViewModel, ActivityAddCouponBinding>() {

    private val mRequestCreateCouponViewModel: RequestCreateCouponViewModel by lazy { getViewModel<RequestCreateCouponViewModel>() }

    override fun layoutId(): Int = R.layout.activity_add_coupon

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        //标题
        mToolbarTitle.text = "添加优惠券"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //添加优惠券
        mRequestCreateCouponViewModel.saveResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val jsonObject = JSON.parseObject(it.string())
                val code = jsonObject.getIntValue("code")
                if (code == 1) {
                    //保存成功

                }
                ToastUtils.showLong(jsonObject.getString("msg"))
            })
        })
    }

    inner class ProxyClick {
        //保存
        fun save() {
            val requestCreateCouponInfo = RequestCreateCouponInfo(
                mViewModel.name.value, mViewModel.distributionTotal.value.toInt(),
                mViewModel.creditAmount.value.toInt(), 10, 1,
                "2020-06-31 00:00:00",
                "2020-06-01 00:00:00"
            )
            mRequestCreateCouponViewModel.createCouponActivity(requestCreateCouponInfo)
        }
    }


}