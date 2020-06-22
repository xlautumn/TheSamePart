package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.same.part.assistant.R
import com.same.part.assistant.activity.SearchActivity
import com.same.part.assistant.adapter.PurchaseFirstLevelAdapter
import com.same.part.assistant.adapter.PurchaseProductAdapter
import com.same.part.assistant.adapter.PurchaseSecondLevelAdapter
import com.same.part.assistant.data.model.CategoryData
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.manager.PurchaseProductManager
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import kotlinx.android.synthetic.main.fragment_purchase.*
import me.hgj.jetpackmvvm.ext.getAppViewModel

/**
 * 采购
 */
class PurchaseFragment : Fragment(), View.OnClickListener {
    companion object {
        const val ANIMATION_DURATION = 200L
    }

    private var mSecondLevelAdapter: PurchaseSecondLevelAdapter? = null
    private var mFirstLevelAdapter: PurchaseFirstLevelAdapter? = null
    private var mProductAdapter: PurchaseProductAdapter? = null
    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutSearch.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firstLevelList.apply {
            mFirstLevelAdapter = activity?.let { PurchaseFirstLevelAdapter(it) }
            setOnItemClickListener { parent, view, position, id ->
                refreshCategory(position)
            }
            adapter = mFirstLevelAdapter
        }

        secondLevelListView.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            mSecondLevelAdapter = activity?.let { PurchaseSecondLevelAdapter(it) }
            mSecondLevelAdapter?.setOnItemClickListener { position, categorySecondData ->
                refreshSecondCategory(position)
            }
            adapter = mSecondLevelAdapter
        }
        productList.apply {
            mProductAdapter = activity?.let { PurchaseProductAdapter(it, ProxyClick(),requestCartViewModel) }
            adapter = mProductAdapter
            mProductAdapter
        }


        PurchaseProductManager.INSTANCE.syncPurchaseCategoryData {
            refreshCategory(0)
        }
        requestCartViewModel.cartProductList.observe(viewLifecycleOwner, Observer {
            mProductAdapter?.notifyDataSetChanged()
        })

    }

    private fun refreshSecondCategory(position: Int) {
        val categoryDataList = PurchaseProductManager.INSTANCE.getPurchaseCategoryData()
        categoryDataList.takeIf { PurchaseProductManager.INSTANCE.mCurrentCategoryFirstLevel in categoryDataList.indices }
            ?.let { dataList ->
                val categoryData =
                    dataList[PurchaseProductManager.INSTANCE.mCurrentCategoryFirstLevel]
                categoryData.let { data ->
                    data.sons?.takeIf { position in it.indices }?.apply {
                        for (i in 0 until size) this[i].let {
                            it.isSelected = (i == position)
                        }
                        mSecondLevelAdapter?.setData(this)
                        //请求产品列表
                        if (0 in this.indices) {
                            refreshProduct(get(position))
                        }
                    }
                }
            }
    }

    private fun refreshCategory(position: Int) {
        PurchaseProductManager.INSTANCE.mCurrentCategoryFirstLevel = position
        val categoryDataList = PurchaseProductManager.INSTANCE.getPurchaseCategoryData()
        for (i in 0 until categoryDataList.size) categoryDataList[i].let {
            it.isSelected = (i == position)
        }
        mFirstLevelAdapter?.setData(categoryDataList)
        mSecondLevelAdapter?.setData(arrayListOf())
        mProductAdapter?.setData(arrayListOf())
        categoryDataList.takeIf { position in categoryDataList.indices }?.let {
            val sons = categoryDataList[position].sons
            sons?.apply {
                for (i in 0 until size) this[i].let {
                    it.isSelected = (i == 0)
                }
                mSecondLevelAdapter?.setData(this)
                if (0 in this.indices) {
                    refreshProduct(get(0))
                }
            }
        }
    }

    private fun refreshProduct(data: CategoryData) {
        PurchaseProductManager.INSTANCE.syncPurchaseProductData(
            data.productCategoryId,
            data.name
        ) {
            //刷新产品数据
            val productData =
                PurchaseProductManager.INSTANCE.getPurchaseProductData()
            emptyView.visibility = if (productData.isNullOrEmpty()) View.VISIBLE else View.GONE
            mProductAdapter?.setData(productData)
        }
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.layoutSearch -> {
                //跳转search
                activity?.let {
                    startActivity(Intent(it, SearchActivity::class.java))
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        requestCartViewModel.clearCacheData()
    }
    inner class ProxyClick {
        /**
         * 添加购物车
         */
        fun addShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.addShopProduct(shopProduct)
        }

        /**
         * 减少购物车数量
         */
        fun minusShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.minusShopProduct(shopProduct)
        }
    }
}