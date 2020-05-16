package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 添加商品分类
 */
class AddProductClassificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_classification)
        //标题
        mToolbarTitle.text = "添加商品分类"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

    }
}