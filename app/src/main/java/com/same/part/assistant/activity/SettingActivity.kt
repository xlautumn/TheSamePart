package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.helper.detectVersion
import com.same.part.assistant.helper.getVersionName
import com.same.part.assistant.ui.login.LoginActivity
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
        //版本检测
        versionDetect.setOnClickListener {
            detectVersion(this)
        }
        //当前版本
        currentVersion.text = "V${getVersionName(this)}"
        //退出登录
        accountQuit.setOnClickListener {
            CacheUtil.setShopUserModel(null)
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }
    }
}