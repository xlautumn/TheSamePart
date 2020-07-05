package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddCashierGoodActivity
import com.same.part.assistant.adapter.CashierFirstLevelAdapter
import com.same.part.assistant.adapter.CashierProductAdapter
import com.same.part.assistant.adapter.CashierSecondLevelAdapter
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.data.model.CashierAllProduct
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.CustomCategory
import com.same.part.assistant.data.model.GetCashierCategoryDetail
import com.same.part.assistant.databinding.FragmentCashierV2Binding
import com.same.part.assistant.viewmodel.request.RequestCashierViewModel
import me.hgj.jetpackmvvm.ext.parseState


/**
 * 收银商品
 */
class CashierFragmentV2 : BaseFragment<RequestCashierViewModel, FragmentCashierV2Binding>() {

    private val mFirstLevelAdapter by lazy {
        CashierFirstLevelAdapter(Proxy()).apply {
            setOnItemClickListener { adapter, view, position ->
                val customCategory = adapter.data[position] as CustomCategory
                mViewModel.firstCategoryId.value = customCategory.customCategoryId
            }
        }
    }
    private val mSecondLevelAdapter by lazy {
        CashierSecondLevelAdapter(Proxy()).apply {
            setOnItemClickListener { adapter, view, position ->
                val customCategory = adapter.data[position] as CustomCategory
                mViewModel.currentSecondCategoryId.value = customCategory.customCategoryId
            }
        }
    }
    private val mCashierProductAdapter by lazy {
        CashierProductAdapter(Proxy()).apply {
            addChildClickViewIds(R.id.tv_operation)
            setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.tv_operation) {
                    val cashierProduct = adapter.data[position] as CashierProduct
                    mViewModel.updateProduct(cashierProduct, onSuccess = {
//                        mViewModel.queryShopCategoryDetail()
                        ToastUtils.showShort(it)
                        cashierProduct.state = if (cashierProduct.state == "1") "0" else "1"
                        adapter.notifyItemChanged(position)
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
    }

    override fun layoutId(): Int = R.layout.fragment_cashier_v2


    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.rvFirstLevel.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mFirstLevelAdapter
        }
        mDatabind.rvSecondLevel.apply {
            layoutManager = LinearLayoutManager(
                context,
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = mSecondLevelAdapter
        }
        mDatabind.cashierRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mCashierProductAdapter
            setEmptyView(mDatabind.emptyView)
        }

        mDatabind.mSmartRefreshLayout.setEnableLoadMore(false)
        mDatabind.mSmartRefreshLayout.setOnRefreshListener {
            mViewModel.queryShopCategoryDetail()
        }
    }

    override fun lazyLoadData() {
        mViewModel.requestCashierClassification()
    }

    override fun createObserver() {
        mViewModel.resultState.observe(viewLifecycleOwner, Observer {
            parseState(it, onSuccess = {
                if (mViewModel.getCashierAllProduct()?.categoryList != it) {
                    setData(CashierAllProduct(it))
                } else {
                    setData(mViewModel.getCashierAllProduct())
                }
            }, onError = {
                ToastUtils.showShort(it.errorMsg)
            })
        })

        mViewModel.productResultState.observe(viewLifecycleOwner, Observer {
            parseState(it, onSuccess = { getCashierCategoryDetail ->
                mDatabind.mSmartRefreshLayout.finishRefresh()
                getCashierCategoryDetail.customCategoryProducts.map { it.product }.let {
                    mViewModel.getProductList(getCashierCategoryDetail.customCategoryId).apply {
                        clear()
                        addAll(it)
                        mCashierProductAdapter.setList(it)
                    }
                }
            }, onError = {
                mDatabind.mSmartRefreshLayout.finishRefresh()
                ToastUtils.showShort(it.errorMsg)
            })
        })
        mViewModel.firstCategoryId.observe(viewLifecycleOwner, Observer {
            mFirstLevelAdapter.notifyDataSetChanged()
            val list = mViewModel.getSecondTitleList(it)
            if (list.isNullOrEmpty()) {
                mViewModel.currentSecondCategoryId.value = ""
                mSecondLevelAdapter.setList(arrayListOf())
            } else {
                mViewModel.currentSecondCategoryId.value = list[0].customCategoryId
                mSecondLevelAdapter.setList(list)
            }
        })
        mViewModel.currentSecondCategoryId.observe(viewLifecycleOwner, Observer {
            mSecondLevelAdapter.notifyDataSetChanged()
            if (it.isNullOrEmpty()) {
                mCashierProductAdapter.setList(arrayListOf())
            } else {
                val list = mViewModel.getProductList(it)
                if (list.isNullOrEmpty()) {
                    mCashierProductAdapter.setList(arrayListOf())
                    mViewModel.queryShopCategoryDetail()
                } else {
                    mCashierProductAdapter.setList(list)
                }
            }
        })
    }

    private fun setData(cashierAllProduct: CashierAllProduct?) {
        cashierAllProduct?.let {
            mViewModel.setCashierAllProduct(it)
            if (it.categoryList.isNullOrEmpty()) {
                mViewModel.firstCategoryId.value = ""
            } else {
                mViewModel.firstCategoryId.value = it.categoryList[0].customCategoryId
            }
            mFirstLevelAdapter.setList(it.categoryList)
        }

    }


    inner class Proxy {
        fun getCurrentSecondCategoryId() = mViewModel.currentSecondCategoryId.value
        fun getCurrentFirstCategoryId() = mViewModel.firstCategoryId.value
    }

}