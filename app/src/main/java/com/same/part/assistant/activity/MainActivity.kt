package com.same.part.assistant.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.BuildConfig
import com.same.part.assistant.R
import com.same.part.assistant.fragment.GoodsFragment
import com.same.part.assistant.fragment.MyFragment
import com.same.part.assistant.fragment.PurchaseFragment
import com.same.part.assistant.helper.detectVersion
import com.same.part.assistant.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 主页面
 */
class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewPager.apply {
            adapter = TabAdapter(supportFragmentManager)
            offscreenPageLimit = TITLES.size
        }
        mTabLayout.apply {
            setupWithViewPager(mViewPager)
            addOnTabSelectedListener(this@MainActivity)
        }
        setupTabLayout()
        //初始化的时候隐藏加号和搜索
        mToolbarAdd.visibility = View.GONE
        mToolbarSearch.apply {
            visibility = View.GONE
            setOnClickListener {
                mSearchView.visibility = View.VISIBLE
            }
        }
        //搜索按钮
        mSearchEdit.apply {
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                    hideKeyboard(v)
                    if (text.isNotEmpty()) {
                        (mFragmentList[TAB_GOODS_INDEX] as? GoodsFragment)?.apply {
                            if (mCurrentTab == GoodsFragment.TAB_CASHIER_INDEX) {
                                this.searchData(GoodsFragment.TAB_CASHIER_INDEX, text.toString())
                            } else {
                                this.searchData(GoodsFragment.TAB_PRODUCT_INDEX, text.toString())
                            }
                        }
                    }
                    true
                } else {
                    false
                }
            }
            hint = "搜索商品名称"
        }
        //取消按钮
        mCancelSearch.setOnClickListener {
            mSearchView.visibility = View.GONE
            mSearchEdit.setText("")
            (mFragmentList[TAB_GOODS_INDEX] as? GoodsFragment)?.apply {
                if (mCurrentTab == GoodsFragment.TAB_CASHIER_INDEX) {
                    this.cancelSearch(GoodsFragment.TAB_CASHIER_INDEX)
                } else {
                    this.cancelSearch(GoodsFragment.TAB_PRODUCT_INDEX)
                }
            }
        }
        //版本检测
        detectVersion(this)
        if (BuildConfig.IS_TEST_URL){
            ToastUtils.showShort("亲,当前是测试环境哦，请放心大胆的操作！")
        }
    }

    /**
     * 隐藏软键盘
     * @param context :上下文
     * @param view    :一般为EditText
     */
    private fun hideKeyboard(view: View) {
        val manager: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    class TabAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = mFragmentList[position]

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
        //如果在商品Tab需要显示天添加和搜索按钮
        if (tab?.position == TAB_GOODS_INDEX) {
            val fragment = mFragmentList[TAB_GOODS_INDEX] as GoodsFragment
            //添加商品分类
            mToolbarAdd.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    if (fragment.mCurrentTab == GoodsFragment.TAB_CASHIER_INDEX) {
                        startActivity(
                            Intent(context, AddCashierGoodActivity::class.java).apply {
                                putExtra(
                                    AddCashierGoodActivity.JUMP_FROM_TYPE,
                                    AddCashierGoodActivity.JUMP_FROM_ADD_CASHIER_GOOD
                                )
                            }
                        )
                    } else {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                AddProductClassificationActivity::class.java
                            ).apply {
                                putExtra(
                                    AddProductClassificationActivity.JUMP_FROM_TYPE,
                                    AddProductClassificationActivity.JUMP_FROM_ADD_SECOND_CATEGORY
                                )
                                putExtra(
                                    AddProductClassificationActivity.PARENT_ID,
                                    ""
                                )
                            }
                        )
                    }
                }
            }
            //搜索商品
            mToolbarSearch.apply {
                visibility = View.VISIBLE
            }
        } else {
            mToolbarAdd.visibility = View.GONE
            mToolbarSearch.visibility = View.GONE
            mSearchView.visibility = View.GONE
        }
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

    /**
     * 改变搜索的提示
     */
    fun changeSearchHint() {
        //改变搜索的hint提示
        val fragment = mFragmentList[TAB_GOODS_INDEX] as? GoodsFragment
        if (fragment?.mCurrentTab == GoodsFragment.TAB_CASHIER_INDEX) {
            mSearchEdit.hint = "搜索商品名称"
        } else {
            mSearchEdit.hint = "搜索分类名称"
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

        //页面Fragment
        val mFragmentList =
            arrayOf(HomeFragment(), GoodsFragment(), PurchaseFragment(), MyFragment())
        const val TAB_HOME_INDEX = 0
        const val TAB_GOODS_INDEX = 1
        const val TAB_PURCHASE_INDEX = 2
        const val TAB_MY_INDEX = 3
    }
}