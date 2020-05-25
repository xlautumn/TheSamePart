package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.same.part.assistant.R
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.ui.login.LoginActivity

/**
 * 启动页面
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ImmersionBar.with(this).transparentNavigationBar().fullScreen(true).statusBarColor(R.color.colorPrimary).init()
        Handler().postDelayed({
            if (CacheUtil.isLogin() && CacheUtil.getTokenExpirationTime() > System.currentTimeMillis()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
            //带点渐变动画
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 300)
    }
}