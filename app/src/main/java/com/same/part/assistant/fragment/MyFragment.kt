package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.activity.*
import com.same.part.assistant.app.util.CacheUtil
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.userAvatar

/**
 * 我的
 */
class MyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //用户名
        userName.text = CacheUtil.getShopName()
        //手机号
        userMobile.text = CacheUtil.getShopPhone()
        //头像
        Glide.with(this)
            .load(CacheUtil.getShopImg())
            .into(userAvatar)
        //收银订单
        cashierOrder.setOnClickListener {
            startActivity(Intent(activity, CashierOrderActivity::class.java))
        }
        //采购订单
        purchaseOrder.setOnClickListener {
            startActivity(Intent(activity, PurchaseOrderActivity::class.java))
        }
        //会员卡管理
        vipCardManager.setOnClickListener {
            startActivity(Intent(activity, VipCardManagerActivity::class.java))
        }
        //优惠券管理
        couponManager.setOnClickListener {
            startActivity(Intent(activity, CouponManagerActivity::class.java))
        }
        //设置
        setting.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }
    }
}