package com.same.part.assistant.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.same.part.assistant.R
import com.same.part.assistant.activity.*
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.data.model.ShopInfoEvent
import com.same.part.assistant.databinding.FragmentHomeBinding
import com.same.part.assistant.viewmodel.state.HomeViewModel
import kotlinx.android.synthetic.main.activity_coupon_manager.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 首页
 */
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
    }

    override fun lazyLoadData() {
        mViewModel.imageUrl.set(shareViewModel.shopPortrait.value)
        mViewModel.name.set(shareViewModel.shopName.value)
    }

    inner class ProxyClick {
        /** 店铺管理 */
        fun goShopManager() {
            startActivity(Intent(activity, ShopManagerActivity::class.java))
        }

        /** 客户管理 */
        fun goCustomManager() {
            startActivity(Intent(activity, CustomManagerActivity::class.java))
        }

        /** 会员管理 */
        fun goVipManager() {
            startActivity(Intent(activity, VipManagerActivity::class.java))
        }
        /** 交易管理 */
        fun goBusinessManager() {
            startActivity(Intent(activity, BusinessManagerActivity::class.java))
        }
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