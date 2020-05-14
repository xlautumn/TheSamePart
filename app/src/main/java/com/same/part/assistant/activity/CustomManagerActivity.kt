package com.same.part.assistant.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.model.CustomInfoModel
import kotlinx.android.synthetic.main.activity_custom_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 店铺管理
 */
class CustomManagerActivity : AppCompatActivity() {
    private val mCustomList = arrayListOf<CustomInfoModel>().apply {
        add(
            CustomInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "多多",
                "511313"
            )
        )
        add(
            CustomInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "时间",
                "21"
            )
        )
        add(
            CustomInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "果园",
                "5113"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_manager)
        //标题
        mToolbarTitle.text = "客户管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mManagerRecyclerView.apply {
            adapter = CustomAdapter(mCustomList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    class CustomAdapter(var dataList: ArrayList<CustomInfoModel>) :
        RecyclerView.Adapter<CustomItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder =
            CustomItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.custom_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: CustomItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holder.itemView.context)
                .load(model.avatarUrl)
                .into(holder.userAvatar)
            holder.userNickname.text = model.nickname
            holder.userId.text = model.userId
        }

    }

    class CustomItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        var userNickname: TextView = itemView.findViewById(R.id.userNickname)
        var userId: TextView = itemView.findViewById(R.id.userId)
    }

}