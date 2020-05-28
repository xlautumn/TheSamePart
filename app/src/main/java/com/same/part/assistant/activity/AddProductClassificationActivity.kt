package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 添加商品分类
 */
class AddProductClassificationActivity : AppCompatActivity() {
    /**
     * 跳转来源
     */
    private lateinit var mJumpFromType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_classification)
        mJumpFromType = when (intent?.getStringExtra(JUMP_FROM_TYPE).orEmpty()) {
            JUMP_FROM_ADD_SECOND_CATEGORY -> {
                //添加二级标题
                mToolbarTitle.text = "添加商品分类"
                JUMP_FROM_ADD_SECOND_CATEGORY
            }
            else -> {
                //编辑标题
                mToolbarTitle.text = "编辑商品分类"
                JUMP_FROM_EDIT
            }
        }
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        //跳转来源
        const val JUMP_FROM_TYPE = "JUMP_FROM_TYPE"
        //从添加二级跳转过来
        const val JUMP_FROM_ADD_SECOND_CATEGORY = "JUMP_FROM_ADD_SECOND_CATEGORY"
        //从编辑跳转过来
        const val JUMP_FROM_EDIT = "JUMP_FROM_EDIT"
    }
}