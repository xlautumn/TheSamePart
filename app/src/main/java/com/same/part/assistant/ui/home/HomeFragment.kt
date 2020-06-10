package com.same.part.assistant.ui.home

import android.content.Intent
import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.BusinessManagerActivity
import com.same.part.assistant.activity.CustomManagerActivity
import com.same.part.assistant.activity.ShopManagerActivity
import com.same.part.assistant.activity.VipManagerActivity
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.ShopInfoEvent
import com.same.part.assistant.databinding.FragmentHomeBinding
import com.same.part.assistant.utils.HttpUtil
import com.same.part.assistant.utils.Util
import com.same.part.assistant.viewmodel.request.RequestShopManagerViewModel
import com.same.part.assistant.viewmodel.state.HomeViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 首页
 */
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    private val mRequestShopManagerViewModel: RequestShopManagerViewModel by lazy { getViewModel<RequestShopManagerViewModel>() }

    override fun layoutId(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        mDatabind.vm = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.imageUrl.set(CacheUtil.getShopImg())
        mViewModel.name.set(CacheUtil.getShopName())
    }

    override fun lazyLoadData() {
        mRequestShopManagerViewModel.shopModelReq(CacheUtil.getToken())
    }

    override fun createObserver() {
        mRequestShopManagerViewModel.shopResult.observe(viewLifecycleOwner, androidx.lifecycle.Observer {resultState ->
            parseState(resultState, {
                CacheUtil.setShopImg(it.img)
                CacheUtil.setShopName(it.name)
                mViewModel.imageUrl.set(it.img)
                mViewModel.name.set(it.name)
            })
        })
    }

    override fun initData() {
        val yesterday = Util.getYesterday()
        val url = StringBuilder("${ApiService.SERVER_URL}shop-summary/searchCollect/")
            .append(CacheUtil.getShopId())
            .append("?year=${yesterday.get(Calendar.YEAR)}")
            .append("&month=${yesterday.get(Calendar.MONTH)+1}")
             .append("&day=${yesterday.get(Calendar.DAY_OF_MONTH)}")
//            .append("&day=8")
            .append("&week=${yesterday.get(Calendar.WEEK_OF_YEAR)}")
        HttpUtil.instance.getUrlWithHeader("WSCX", CacheUtil.getToken(), url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.getJSONObject("msg")?.apply {
                    mViewModel.turnoverCountToday.set(getString("turnoverCountToday") ?: "0")
                    mViewModel.incomeToday.set(getString("incomeToday") ?: "0")
                    mViewModel.turnoverCountWeek.set(getString("turnoverCountWeek") ?: "0")
                    mViewModel.incomeWeek.set(getString("incomeWeek") ?: "0")
                    mViewModel.incomeWeekAvg.set(Util.format2(getString("incomeWeekAvg") ?: "0"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
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