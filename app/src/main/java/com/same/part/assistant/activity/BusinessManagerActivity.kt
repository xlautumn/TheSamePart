package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.activity_business_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 交易管理
 */
class BusinessManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_manager)
        //标题
        mToolbarTitle.text = "交易管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //交易明细
        businessDetail.setOnClickListener {
            startActivity(Intent(this, BusinessDetailActivity::class.java))
        }
    }

}