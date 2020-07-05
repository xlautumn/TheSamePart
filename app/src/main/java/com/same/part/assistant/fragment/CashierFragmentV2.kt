package com.same.part.assistant.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddCashierGoodActivity
import com.same.part.assistant.activity.SearchCashierProductToEditActivity
import com.same.part.assistant.adapter.CashierFirstLevelAdapter
import com.same.part.assistant.adapter.CashierProductAdapter
import com.same.part.assistant.adapter.CashierSecondLevelAdapter
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.app.ext.showMessage
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
        CashierProductAdapter().apply {
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

            setOnItemLongClickListener { adapter, view, position ->
                val cashierProduct = adapter.data[position] as CashierProduct
                showMessage("请确认是否删除该商品", positiveAction = {
                    mViewModel.delCashierProduct(cashierProduct, onSuccess = {
//                        mViewModel.queryShopCategoryDetail()
                        ToastUtils.showShort(it)
                        mViewModel.getProductList(mViewModel.currentSecondCategoryId.value).removeAt(position)
                        adapter.data.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }, onError = {
                        ToastUtils.showShort(it)
                    })
                }, negativeButtonText = "取消")
                true
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

        mDatabind.layoutSearch.setOnClickListener {
            startActivityForResult(
                Intent(context, SearchCashierProductToEditActivity::class.java),
                REQUEST_CODE_SEARCH_PRODUCT_TO_EDIT
            )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SEARCH_PRODUCT_TO_EDIT) {
                val list =
                    data?.getSerializableExtra(KEY_CASHIER_PRODUCT_CHANGE_LIST) as? ArrayList<CashierProduct>
                val delList =
                    data?.getSerializableExtra(KEY_CASHIER_PRODUCT_DEL_LIST) as? ArrayList<CashierProduct>

                mViewModel.updateProductBecauseOfSearch(list, delList) {
                    mCashierProductAdapter.setList(mViewModel.getProductList(mViewModel.currentSecondCategoryId.value))
                }

            }
        }
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

    companion object {
        /**
         * 搜索收银商品（编辑商品）
         */
        const val REQUEST_CODE_SEARCH_PRODUCT_TO_EDIT = 300

        /**
         * 发生变化的收银商品集合
         */
        const val KEY_CASHIER_PRODUCT_CHANGE_LIST = "KEY_CASHIER_PRODUCT_CHANGE_LIST"

        /**
         * 已删除的收银商品集合
         */
        const val KEY_CASHIER_PRODUCT_DEL_LIST = "KEY_CASHIER_PRODUCT_DEL_LIST"
    }

}