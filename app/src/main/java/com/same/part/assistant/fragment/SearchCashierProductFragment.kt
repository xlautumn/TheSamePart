package com.same.part.assistant.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.viewmodel.request.RequestCashierProductViewModel
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 搜索商品(添加适用商品)
 */
class SearchCashierProductFragment :
    SearchBaseFragment<RequestCashierProductViewModel, CashierProduct>() {
    private lateinit var suitableProductViewModel: SuitableProductViewModel
    private val mAdapter = SelectSuitableProductFragment.CustomerAdapter()
    override fun initRecyclerView(recyclerView: RecyclerView) {
        mAdapter.setOnItemClickListener { adapter, view, position ->
            view.isSelected = !view.isSelected
            (adapter.data[position] as SelectSuitableProductFragment.CashierProductWrap).isSelect = view.isSelected
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {
            suitableProductViewModel = getViewModel<SuitableProductViewModel>()

            mViewModel.cashierProductResultState.observe(viewLifecycleOwner, Observer {
                parseState(it, onSuccess = {
                        mViewModel.setSearchResultList(it.data)

                }, onError = {
                    ToastUtils.showShort(it.errorMsg)
                })
            })
        }
    }

    override fun updateRecyclerView(data: List<CashierProduct>) {
        data.map {
            val hasAdd = suitableProductViewModel.hasAdd(it)
            SelectSuitableProductFragment.CashierProductWrap(it, hasAdd)
        }.let {
            mAdapter.setList(it)
        }
    }

    fun getAdapter() = mAdapter

}