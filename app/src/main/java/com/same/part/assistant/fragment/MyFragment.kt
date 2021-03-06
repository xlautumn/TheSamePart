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
import com.same.part.assistant.viewmodel.request.RequestShopManagerViewModel
import com.same.part.assistant.viewmodel.state.HomeViewModel
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.fragment_my.userAvatar
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 我的
 */
class MyFragment : BaseFragment<HomeViewModel, FragmentMyBinding>(){

    private val mRequestShopManagerViewModel: RequestShopManagerViewModel by lazy { getViewModel<RequestShopManagerViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_my

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        mDatabind.vm = mViewModel
        //手机号
        mViewModel.phone.set(CacheUtil.getShopPhone())
        mViewModel.imageUrl.set(CacheUtil.getShopImg())
        mViewModel.name.set(CacheUtil.getShopName())
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
        mRequestShopManagerViewModel.shopModelReq(CacheUtil.getToken())
    }

    override fun createObserver() {
        mRequestShopManagerViewModel.shopResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {resultState ->
            parseState(resultState, {
                CacheUtil.setShopImg(it.img)
                CacheUtil.setShopName(it.name)
                mViewModel.imageUrl.set(it.img )
                mViewModel.name.set(it.name)
            })
        })
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