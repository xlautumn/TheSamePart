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
import com.same.part.assistant.data.model.VipInfoModel
import kotlinx.android.synthetic.main.activity_custom_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 会员管理
 */
class VipManagerActivity : AppCompatActivity() {
    private val mVipList = arrayListOf<VipInfoModel>().apply {
        add(
            VipInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "多多",
                "511313",
                "金卡",
                "500"
            )
        )
        add(
            VipInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "时间",
                "21",
                "银卡",
                "1500"
            )
        )
        add(
            VipInfoModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "果园",
                "5113",
                "金卡",
                "600"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_manager)
        //标题
        mToolbarTitle.text = "会员管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mManagerRecyclerView.apply {
            adapter = CustomAdapter(mVipList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    class CustomAdapter(var dataList: ArrayList<VipInfoModel>) :
        RecyclerView.Adapter<VipItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipItemHolder =
            VipItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.vip_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: VipItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holder.itemView.context)
                .load(model.avatarUrl)
                .into(holder.userAvatar)
            holder.userNickname.text = model.nickname
            holder.userId.text = model.userId
            holder.vipCard.text = model.vipCard
            holder.vipBalance.text = model.vipBalance
        }

    }

    class VipItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        var userNickname: TextView = itemView.findViewById(R.id.userNickname)
        var userId: TextView = itemView.findViewById(R.id.userId)
        var vipCard: TextView = itemView.findViewById(R.id.vipCard)
        var vipBalance: TextView = itemView.findViewById(R.id.vipBalance)
    }

}