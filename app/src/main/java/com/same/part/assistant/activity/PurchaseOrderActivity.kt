package com.same.part.assistant.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.R
import com.same.part.assistant.fragment.OrderStatusTabFragment
import kotlinx.android.synthetic.main.activity_purchase_order.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 采购订单
 */
class PurchaseOrderActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_order)
        //标题
        mToolbarTitle.text = "采购订单"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        mViewPager.apply {
            adapter = TabAdapter(supportFragmentManager)
            offscreenPageLimit = TITLES.size
        }
        mTabLayout.apply {
            setupWithViewPager(mViewPager)
            addOnTabSelectedListener(this@PurchaseOrderActivity)
        }
    }

    inner class TabAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment =
            OrderStatusTabFragment(this@PurchaseOrderActivity, TITLES[position])

        override fun getCount(): Int = TITLES.size

        override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }

    companion object {
        //标题
        val TITLES = arrayOf("待付款", "待发货", "待收货", "取消", "完成")
    }
}