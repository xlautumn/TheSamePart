package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 修改密码
 */
class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        //标题
        mToolbarTitle.text = "修改密码"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
    }

}