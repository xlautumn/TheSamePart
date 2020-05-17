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
import com.same.part.assistant.model.CouponInfoModel
import kotlinx.android.synthetic.main.activity_coupon_manager.*

/**
 * 优惠券管理
 */
class CouponManagerActivity : AppCompatActivity() {
    private val mCouponList = arrayListOf<CouponInfoModel>().apply {
        add(
            CouponInfoModel(
                "中秋优惠券",
                "50",
                "200",
                "20",
                "未开始"
            )
        )
        add(
            CouponInfoModel(
                "端午优惠券",
                "50",
                "200",
                "20",
                "未开始"
            )
        )
        add(
            CouponInfoModel(
                "国庆优惠券",
                "50",
                "200",
                "20",
                "未开始"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_manager)
        //标题
        mToolbarTitle.text = "优惠券管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //添加优惠券
        mToolbarAdd.setOnClickListener {
            startActivity(Intent(this@CouponManagerActivity, AddCouponActivity::class.java))
        }
        //列表数据
        mCouponRecyclerView.apply {
            adapter = CustomAdapter(mCouponList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    class CustomAdapter(var dataList: ArrayList<CouponInfoModel>) :
        RecyclerView.Adapter<CouponItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponItemHolder =
            CouponItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.coupon_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: CouponItemHolder, position: Int) {
            val model = dataList[position]
            holder.couponName.text = model.name
            holder.couponCount.text = "${model.remain}/${model.total}"
            holder.couponUse.text = model.used
            holder.couponStatus.text = model.status
            holder.couponOperation.apply {
                setOnClickListener {

                }
            }
        }

    }

    class CouponItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var couponName: TextView = itemView.findViewById(R.id.couponName)
        var couponCount: TextView = itemView.findViewById(R.id.couponCount)
        var couponUse: TextView = itemView.findViewById(R.id.couponUse)
        var couponStatus: TextView = itemView.findViewById(R.id.couponStatus)
        var couponOperation: View = itemView.findViewById(R.id.llFive)
    }

}