package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.R
import com.same.part.assistant.databinding.FragmentSelectCustomBinding

/**
 * 选择客户
 */
class SelectCustomFragment : Fragment() , TabLayout.OnTabSelectedListener {
    private lateinit var binding: FragmentSelectCustomBinding

    private val mFragmentList :List<Fragment> by lazy { arrayListOf(CashierFragment(), ProductClassificationFragment()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectCustomBinding.inflate(inflater, container, false)
        binding.titleBack.setOnClickListener { findNavController().navigateUp() }
        binding.toolbarSearch.setOnClickListener {
            findNavController().navigate(R.id.action_selectCustomFragment_to_searchBaseFragment)
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.customViewPager.apply {
            adapter = TabAdapter(childFragmentManager,mFragmentList)
            offscreenPageLimit = GoodsFragment.TITLES.size
        }
        binding.customTabs.apply {
            setupWithViewPager(binding.customViewPager)
            addOnTabSelectedListener(this@SelectCustomFragment)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    class TabAdapter(fragmentManager: FragmentManager,private val fragmentList :List<Fragment>) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        override fun getPageTitle(position: Int): CharSequence? = TITLES[position]

    }

    companion object {
        //标题
       private val TITLES = arrayOf("普通客户", "会员卡客户")
    }
}