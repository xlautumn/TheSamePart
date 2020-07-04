package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.same.part.assistant.R
import com.same.part.assistant.app.ext.appendImageScaleSuffix
import com.same.part.assistant.data.model.Customer
import com.same.part.assistant.data.model.MemberCardModel
import com.same.part.assistant.databinding.FragmentSelectCustomBinding
import com.same.part.assistant.viewmodel.state.ProvideCouponViewModel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.state.ResultState

/**
 * 选择客户
 */
class SelectCustomFragment : Fragment() {

    private val pageSize = 50
    private var pageNo = 0
    private var isRefresh = false
    private var isLoadMore = false

    private lateinit var binding: FragmentSelectCustomBinding
    private var customerType = CUSTOMER_TYPE_GENERAL

    private lateinit var provideCouponViewModel: ProvideCouponViewModel
    private val mAdapter = CustomerAdapter()

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

        binding.tvConfirm.setOnClickListener {
            mAdapter.data.filter { !it.hasAdd && it.isSelect }.map { it.customer }.let {
                provideCouponViewModel.addCustomListToProvider(it)
            }
            findNavController().navigateUp()
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            view.isSelected = !view.isSelected
            (adapter.data[position] as CustomerWrap).isSelect = view.isSelected
        }

        binding.rvCustomer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            setEmptyView(binding.emptyView)
        }

        customerType = arguments?.getInt(KEY_CUSTOMER_TYPE) ?: CUSTOMER_TYPE_GENERAL

        binding.smartRefreshLayout.setEnableLoadMore(false)
        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (!isRefresh && !isLoadMore) {
                    isLoadMore = true
                    provideCouponViewModel.requestCustomerList(
                        customerType
                    )
                }

            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                if (!isRefresh && !isLoadMore) {
                    refreshData()
                }
            }

        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {
            provideCouponViewModel = getViewModel<ProvideCouponViewModel>()
            //注册请求数据观察者
            provideCouponViewModel.getResultState(customerType)
                .observe(viewLifecycleOwner, Observer {
                    if (it is ResultState.Loading) {

                    }
                    if (it is ResultState.Success<List<Customer>>) {

                        if (isRefresh) {
                            isRefresh = false
                            binding.smartRefreshLayout.finishRefresh()
                            binding.smartRefreshLayout.resetNoMoreData()

                            provideCouponViewModel.setCustomerList(it.data,customerType,true)

                        }
                        if (isLoadMore) {
                            isLoadMore = false
                            pageNo++

                            if (it.data.isNullOrEmpty()) {
                                ToastUtils.showShort("没有更多商品了")
                                binding.smartRefreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                provideCouponViewModel.setCustomerList(it.data,customerType,false)
                                binding.smartRefreshLayout.finishLoadMore()
                            }

                        }
                    }
                    if (it is ResultState.Error) {
                        ToastUtils.showShort(it.error.errorMsg)
                    }

                })

            //注册数据集合观察者
            provideCouponViewModel.getCustomerList(customerType).observe(viewLifecycleOwner,
                Observer {
                    setData(it)
                })

            provideCouponViewModel.getCustomerList(customerType).value?.apply {
                setData(this)
            }?: kotlin.run {
                refreshData()
            }
        }
    }

    private fun setData(data:List<Customer>){
        data.map {
            val hasAdd = provideCouponViewModel.hasAdd(it)
            CustomerWrap(it, hasAdd)
        }.also { mAdapter.setList(it) }
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        isRefresh = true
        pageNo = 0
        provideCouponViewModel.requestCustomerList(customerType)
    }

    companion object {
        const val KEY_CUSTOMER_TYPE = "KEY_CUSTOMER_TYPE"

        const val CUSTOMER_TYPE_GENERAL = 0 //普通客户
        const val CUSTOMER_TYPE_MEMBER = 1 //会员客户
    }


    class CustomerAdapter() :
        BaseQuickAdapter<CustomerWrap, BaseViewHolder>(R.layout.customer_item, null) {

        override fun convert(holder: BaseViewHolder, item: CustomerWrap) {
            holder.getView<ImageView>(R.id.photo).let {
                Glide.with(holder.itemView.context)
                    .load(it.appendImageScaleSuffix(item.customer.photo))
                    .into(it)
            }
            holder.setText(R.id.name, item.customer.nickname)
            holder.setText(R.id.mobile, item.customer.mobile)
            if (item.hasAdd) {
                holder.itemView.isSelected = true
                holder.itemView.isEnabled = false
            } else {
                holder.itemView.isEnabled = true
                holder.itemView.isSelected = item.isSelect
            }
        }
    }

    class CustomerWrap(
        val customer: Customer,//客户对象
        val hasAdd: Boolean//是否已经添加到发放集合
    ) {
        var isSelect = false //当前是否被选中
    }
}

