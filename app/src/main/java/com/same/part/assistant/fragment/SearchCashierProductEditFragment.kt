package com.same.part.assistant.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddCashierGoodActivity
import com.same.part.assistant.adapter.CashierProductAdapter
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.viewmodel.request.RequestCashierProductEditViewModel
import com.same.part.assistant.viewmodel.request.RequestCashierProductViewModel
import com.same.part.assistant.viewmodel.state.SearchCashierProductToEditViewModel
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 搜索商品(编辑商品)
 */
class SearchCashierProductEditFragment :
    SearchBaseFragment<RequestCashierProductEditViewModel, CashierProduct>() {
    private val mAdapter = CashierProductAdapter().apply {
        addChildClickViewIds(R.id.tv_operation)
        setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.tv_operation) {
                val cashierProduct = adapter.data[position] as CashierProduct
                mViewModel.updateProduct(cashierProduct, onSuccess = {
//                        mViewModel.queryShopCategoryDetail()
                    ToastUtils.showShort(it)
                    cashierProduct.state = if (cashierProduct.state == "1") "0" else "1"
                    adapter.notifyItemChanged(position)
                    activity?.let {
                        SearchCashierProductToEditViewModel.get(it)
                            .addChangeCashierProductList(cashierProduct)
                    }
                }, onError = {
                    ToastUtils.showShort(it)
                })
            }
        }

        setOnItemClickListener { adapter, view, position ->
            val cashierProduct = adapter.data[position] as CashierProduct
            startActivity(
                Intent(context, AddCashierGoodActivity::class.java).apply {
                    putExtra(
                        AddCashierGoodActivity.JUMP_FROM_TYPE,
                        AddCashierGoodActivity.JUMP_FROM_EDIT
                    )
                    putExtra(
                        AddCashierGoodActivity.CASHIER_PRODUCT_ID,
                        cashierProduct.productId
                    )
                }
            )
        }
    }

    override fun initRecyclerView(recyclerView: RecyclerView) {

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {

            mViewModel.cashierProductResultState.observe(viewLifecycleOwner, Observer {
                parseState(it, onSuccess = {
                    mViewModel.setSearchResultList(it.data)

                }, onError = {
                    ToastUtils.showShort(it.errorMsg)
                })
            })
        }
    }

    override fun goBack() {
        activity?.onBackPressed()
    }


    override fun updateRecyclerView(data: List<CashierProduct>) {
        mAdapter.setList(data)
    }
}