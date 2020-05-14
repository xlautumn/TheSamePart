package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.same.part.assistant.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ImmersionBar.with(this).init();
    }
}