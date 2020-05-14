package com.same.part.assistant.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.model.VipInfoModel
import kotlinx.android.synthetic.main.activity_custom_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 交易管理
 */
class BusinessDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_detail)
        //标题
        mToolbarTitle.text = "交易明细"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

    }

}