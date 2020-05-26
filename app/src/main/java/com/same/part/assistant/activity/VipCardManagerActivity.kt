package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.VipCardInfoModel
import kotlinx.android.synthetic.main.activity_vip_card_manager.*

/**
 * 会员卡管理
 */
class VipCardManagerActivity : AppCompatActivity() {
    private val mVipList = arrayListOf<VipCardInfoModel>().apply {
        add(
            VipCardInfoModel(
                "金卡",
                "500",
                "7.5",
                "336",
                true
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_card_manager)
        //标题
        mToolbarTitle.text = "会员卡管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //添加会员卡
        mToolbarAdd.apply {
            if (mVipList.size > 1) {
                isEnabled = false
            } else {
                isEnabled = true
                setOnClickListener {
                    it.isEnabled = false
                    startActivity(
                        Intent(
                            this@VipCardManagerActivity,
                            AddVipCardActivity::class.java
                        )
                    )
                }
            }
        }
        //列表数据
        mVipRecyclerView.apply {
            adapter = CustomAdapter(mVipList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    class CustomAdapter(var dataList: ArrayList<VipCardInfoModel>) :
        RecyclerView.Adapter<VipItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipItemHolder =
            VipItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.vip_card_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: VipItemHolder, position: Int) {
            val model = dataList[position]
            holder.vipCardName.text = model.name
            holder.vipCardDenomination.text = model.denomination
            holder.vipCardDiscount.text = model.discount
            holder.vipCardUserCount.text = model.userCount
            holder.vipCardOperation.apply {
                setOnClickListener {
                    model.operation = !model.operation
                    text = if (model.operation) "启动" else "禁用"
                }
            }
        }

    }

    class VipItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vipCardName: TextView = itemView.findViewById(R.id.vipCardName)
        var vipCardDenomination: TextView = itemView.findViewById(R.id.vipCardDenomination)
        var vipCardDiscount: TextView = itemView.findViewById(R.id.vipCardDiscount)
        var vipCardUserCount: TextView = itemView.findViewById(R.id.vipCardUserCount)
        var vipCardOperation: TextView = itemView.findViewById(R.id.vipCardOperation)
    }

}