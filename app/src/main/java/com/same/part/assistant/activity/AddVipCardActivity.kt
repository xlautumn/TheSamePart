package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 添加会员卡
 */
class AddVipCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vip_card)
        //标题
        mToolbarTitle.text = "添加会员卡"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

    }

}