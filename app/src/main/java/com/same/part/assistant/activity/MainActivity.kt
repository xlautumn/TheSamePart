package com.same.part.assistant.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.R
import com.same.part.assistant.fragment.GoodsFragment
import com.same.part.assistant.ui.home.HomeFragment
import com.same.part.assistant.fragment.MyFragment
import com.same.part.assistant.fragment.PurchaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 主页面
 */
class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTitleBack.visibility = View.GONE
        mViewPager.apply {
            adapter = TabAdapter(supportFragmentManager)
            offscreenPageLimit = TITLES.size
        }
        mTabLayout.apply {
            setupWithViewPager(mViewPager)
            addOnTabSelectedListener(this@MainActivity)
        }
        setupTabLayout()
    }

    class TabAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = when (position) {
            TAB_HOME_INDEX -> HomeFragment()
            TAB_GOODS_INDEX -> GoodsFragment()
            TAB_PURCHASE_INDEX -> PurchaseFragment()
            else -> MyFragment()
        }

        override fun getCount(): Int = TITLES.size

        override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.apply {
            changedTabStatus(this, false)
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tab?.apply {
            changedTabStatus(this, true)
        }
        mToolbarTitle.text = TITLES[tab?.position ?: 0]
    }

    /**
     * 自定义TabItem
     */
    private fun setupTabLayout() {
        var i = 0
        val count: Int = mTabLayout.tabCount
        while (i < count) {
            val tab: TabLayout.Tab? = mTabLayout.getTabAt(i)
            if (tab == null) {
                i++
                continue
            }
            tab.customView = createTabView(i)
            i++
        }
        mTabLayout.getTabAt(0)?.apply {
            changedTabStatus(this, true)
            mToolbarTitle.text = TITLES[0]
        }
    }

    /**
     * TabItem自定义View
     *
     * @param position 位置
     * @return View
     */
    private fun createTabView(position: Int): View? =
        LayoutInflater.from(this).inflate(R.layout.layout_nav_tab, null)?.apply {
            findViewById<TextView>(R.id.mTabText).text = TITLES[position]
            findViewById<ImageView>(R.id.mTabIcon).setBackgroundResource(ICONS[position])
        }


    /**
     * 更改TabItem状态
     *
     * @param tab        TabItem
     * @param isSelected 已选中
     */
    private fun changedTabStatus(tab: TabLayout.Tab, isSelected: Boolean) {
        tab.customView?.apply {
            findViewById<TextView>(R.id.mTabText).setTextColor(
                ActivityCompat.getColor(
                    this@MainActivity,
                    if (isSelected) R.color.colorPrimary else R.color.color_999999
                )
            )
            findViewById<ImageView>(R.id.mTabIcon).setBackgroundResource(if (isSelected) ICONS_CHECKED[tab.position] else ICONS[tab.position])
        }
    }

    companion object {
        //底部Tab
        val TITLES = arrayOf("概览", "商品", "采购", "我的")
        //未选中的图标
        val ICONS = arrayOf(R.drawable.home, R.drawable.goods, R.drawable.purchase, R.drawable.my)
        //选中的图标
        val ICONS_CHECKED = arrayOf(
            R.drawable.home_checked,
            R.drawable.goods_checked,
            R.drawable.purchase_checked,
            R.drawable.my_checked
        )
        const val TAB_HOME_INDEX = 0
        const val TAB_GOODS_INDEX = 1
        const val TAB_PURCHASE_INDEX = 2
        const val TAB_MY_INDEX = 3
    }
}