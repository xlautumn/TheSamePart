package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.R
import com.same.part.assistant.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_goods.*

/**
 * 商品
 */
class GoodsFragment : Fragment(), TabLayout.OnTabSelectedListener {
    /**
     * 当前所在的Tab
     */
    internal var mCurrentTab = TAB_CASHIER_INDEX

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goods, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        goodsPager.apply {
            adapter = TabAdapter(childFragmentManager)
            offscreenPageLimit = TITLES.size
        }
        goodsTabs.apply {
            setupWithViewPager(goodsPager)
            addOnTabSelectedListener(this@GoodsFragment)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        mCurrentTab = tab?.position ?: 0
    }

    /**
     * 开始触发搜索逻辑
     */
    fun searchData(index: Int, text: String) {
        if (index == TAB_CASHIER_INDEX) {
            (mFragmentList[0] as? CashierFragment)?.searchData(text, true)
        } else {
            (mFragmentList[1] as? ProductClassificationFragment)?.searchData(text, true)
        }
    }

    fun cancelSearch(index: Int) {
        if (index == TAB_CASHIER_INDEX) {
            (mFragmentList[0] as? CashierFragment)?.cancelSearch()
        } else {
            (mFragmentList[1] as? ProductClassificationFragment)?.cancelSearch()
        }
    }

    class TabAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = mFragmentList[position]

        override fun getCount(): Int = mFragmentList.size

        override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

    }

    companion object {
        //标题
        val TITLES = arrayOf("收银商品", "商品分类")
        const val TAB_CASHIER_INDEX = 0
        const val TAB_PRODUCT_INDEX = 1
        val mFragmentList = arrayOf(CashierFragment(), ProductClassificationFragment())
    }
}