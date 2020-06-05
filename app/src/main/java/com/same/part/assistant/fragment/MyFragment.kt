package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.activity.*
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.ShopInfoEvent
import com.same.part.assistant.databinding.FragmentHomeBinding
import com.same.part.assistant.databinding.FragmentMyBinding
import com.same.part.assistant.viewmodel.state.HomeViewModel
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.userAvatar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 我的
 */
class MyFragment : BaseFragment<HomeViewModel, FragmentMyBinding>(){

    override fun layoutId(): Int = R.layout.fragment_my

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        mDatabind.vm = mViewModel
        //手机号
        mViewModel.phone.set(CacheUtil.getShopPhone())
        //头像
        mViewModel.imageUrl.set(shareViewModel.shopPortrait.value)
        //用户名
        mViewModel.name.set(shareViewModel.shopName.value)
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

    override fun lazyLoadData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRefresh(event: ShopInfoEvent) {
        mViewModel.imageUrl.set(event.img)
        mViewModel.name.set(event.name)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

}