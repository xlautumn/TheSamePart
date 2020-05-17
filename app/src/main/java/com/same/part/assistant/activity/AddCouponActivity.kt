package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 添加会员卡
 */
class AddCouponActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_coupon)
        //标题
        mToolbarTitle.text = "添加优惠券"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

    }

}