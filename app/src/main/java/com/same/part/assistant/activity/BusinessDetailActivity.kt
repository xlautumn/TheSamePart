package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 交易管理
 */
class BusinessDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_detail)
        //标题
        mToolbarTitle.text = "交易明细"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

    }

}