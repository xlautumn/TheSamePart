package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.activity.BusinessManagerActivity
import com.same.part.assistant.activity.CustomManagerActivity
import com.same.part.assistant.activity.ShopManagerActivity
import com.same.part.assistant.activity.VipManagerActivity
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * 首页
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Glide.with(this)
            .load("https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108")
            .into(userAvatar)
        //店铺信息
        shop_info.setOnClickListener {
            startActivity(Intent(activity, ShopManagerActivity::class.java))
        }
        //客户管理
        custom_manager.setOnClickListener {
            startActivity(Intent(activity, CustomManagerActivity::class.java))
        }
        //会员管理
        vip_manager.setOnClickListener {
            startActivity(Intent(activity, VipManagerActivity::class.java))
        }
        //交易管理
        business_manager.setOnClickListener {
            startActivity(Intent(activity, BusinessManagerActivity::class.java))
        }

    }
}