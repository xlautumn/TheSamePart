package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 设置
 */
class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        //标题
        mToolbarTitle.text = "设置"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //账号密码
        accountPassword.setOnClickListener {
            startActivity(Intent(this, AccountPasswordManagerActivity::class.java))
        }
    }

}