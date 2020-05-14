package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 店铺管理
 */
class ShopManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_manager)
        Glide.with(this)
            .load("https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108")
            .into(userAvatar)
        //标题
        mToolbarTitle.text = "店铺管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
    }

}