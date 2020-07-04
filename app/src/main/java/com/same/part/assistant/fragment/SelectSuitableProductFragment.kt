package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.app.ext.appendImageScaleSuffix
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.Customer
import com.same.part.assistant.data.model.GetCashierProductMsg
import com.same.part.assistant.databinding.FragmentSelectSuitableProductBinding
import com.same.part.assistant.viewmodel.state.ProvideCouponViewModel
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.state.ResultState

/**
 * 选择优惠券适用商品
 */
class SelectSuitableProductFragment : Fragment() {

    private val pageSize = 50
    private var pageNo = 0
    private var isRefresh = false
    private var isLoadMore = false

    private lateinit var binding: FragmentSelectSuitableProductBinding

    private lateinit var suitableProductViewModel: SuitableProductViewModel
    private val mAdapter = CustomerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectSuitableProductBinding.inflate(inflater, container, false)
        binding.titleBack.setOnClickListener { findNavController().navigateUp() }

        binding.tvConfirm.setOnClickListener {
            mAdapter.data.filter { !it.hasAdd && it.isSelect }.map { it.product }.let {
                suitableProductViewModel.addProductList(it)
            }
            findNavController().navigateUp()
        }

        mAdapter.setOnItemClickListener { adapter, view, position ->
            view.isSelected = !view.isSelected
            (adapter.data[position] as CashierProductWrap).isSelect = view.isSelected
        }

        binding.rvProduct.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            setEmptyView(binding.emptyView)
        }


        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                if (!isRefresh && !isLoadMore) {
                    isLoadMore = true
                    suitableProductViewModel.getCashierProductList(pageNo + 1, pageSize)
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
            suitableProductViewModel = getViewModel<SuitableProductViewModel>()
            //注册请求数据观察者
            suitableProductViewModel.cashierProductResultState
                .observe(viewLifecycleOwner, Observer {
                    if (it is ResultState.Loading) {

                    }
                    if (it is ResultState.Success<GetCashierProductMsg>) {

                        if (isRefresh) {
                            isRefresh = false
                            binding.smartRefreshLayout.finishRefresh()
                            binding.smartRefreshLayout.resetNoMoreData()

                            suitableProductViewModel.setProductList(it.data.data,  true)

                        }
                        if (isLoadMore) {
                            isLoadMore = false
                            pageNo++

                            if (it.data.data.isNullOrEmpty()) {
                                ToastUtils.showShort("没有更多商品了")
                                binding.smartRefreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                suitableProductViewModel.setProductList(
                                    it.data.data,
                                    false
                                )
                                binding.smartRefreshLayout.finishLoadMore()
                            }

                        }
                    }
                    if (it is ResultState.Error) {
                        ToastUtils.showShort(it.error.errorMsg)
                    }

                })

            //注册数据集合观察者
            suitableProductViewModel.cashierProductList.observe(viewLifecycleOwner, Observer {
                setData(it)
            })

            suitableProductViewModel.cashierProductList.value?.apply {
                setData(this)
            } ?: kotlin.run {
                refreshData()
            }
        }
    }

    private fun setData(data: List<CashierProduct>) {
        data.map {
            val hasAdd = suitableProductViewModel.hasAdd(it)
            CashierProductWrap(it, hasAdd)
        }.also { mAdapter.setList(it) }
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        isRefresh = true
        pageNo = 0
        suitableProductViewModel.getCashierProductList(pageNo,pageSize)
    }


    class CustomerAdapter() :
        BaseQuickAdapter<CashierProductWrap, BaseViewHolder>(
            R.layout.layout_item_select_suitable_product,
            null
        ) {

        override fun convert(holder: BaseViewHolder, item: CashierProductWrap) {
            holder.getView<ImageView>(R.id.photo).let {
                if (item.product.img.isNullOrEmpty()){
                    it.setImageResource(R.drawable.icn_head_img)
                }else {
                    Glide.with(holder.itemView.context)
                        .load(it.appendImageScaleSuffix(item.product.img))
                        .into(it)
                }
            }
            holder.setText(R.id.name, item.product.name)
            holder.setText(R.id.tv_price, "￥${item.product.price}")
            holder.setText(R.id.tv_quantity, "库存${item.product.quantity}${item.product.unit}")
            if (item.hasAdd) {
                holder.itemView.isSelected = true
                holder.itemView.isEnabled = false
            } else {
                holder.itemView.isEnabled = true
                holder.itemView.isSelected = item.isSelect
            }
        }
    }

    class CashierProductWrap(
        val product: CashierProduct,//商品对象
        val hasAdd: Boolean//是否已经添加到适用商品集合
    ) {
        var isSelect = false //当前是否被选中
    }
}

