package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.OrderSubmitActivity
import com.same.part.assistant.activity.SearchActivity
import com.same.part.assistant.adapter.CartProductAdapter
import com.same.part.assistant.adapter.PurchaseSecondLevelAdapter
import com.same.part.assistant.adapter.PurchaseProductAdapter
import com.same.part.assistant.adapter.PurchaseFirstLevelAdapter
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
    private val mCartProductAdapter: CartProductAdapter by lazy { CartProductAdapter(ProxyClick()) }
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
        statement.setOnClickListener(this)
        rootDetail.setOnClickListener(this)
        right_btn.setOnClickListener(this)
        cart_invalid_view.setOnClickListener(this)
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

        pop_list_view.apply {
            adapter = mCartProductAdapter
        }
        PurchaseProductManager.INSTANCE.syncPurchaseCategoryData {
            refreshCategory(0)
        }
        requestCartViewModel.cartProductList.observe(viewLifecycleOwner, Observer {
            mCartProductAdapter.setData(it)
            totalMoney.text = requestCartViewModel.totalPrice
            statement.text = getString(R.string.purchase_statement, requestCartViewModel.totalNum)
            if (it.isEmpty()) {
                pop_list_view.visibility = View.INVISIBLE
                tv_no_data.visibility = View.VISIBLE
                if (cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            } else {
                pop_list_view.visibility = View.VISIBLE
                tv_no_data.visibility = View.INVISIBLE
            }
            mProductAdapter?.notifyDataSetChanged()
        })

        requestCartViewModel.requestCartList()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.statement -> {
                if (requestCartViewModel.cartProductList.value.isNullOrEmpty()) {
                    ToastUtils.showShort("还未添加到购物车")
                } else {
                    startActivity(Intent(context, OrderSubmitActivity::class.java))
                }
            }
            R.id.rootDetail -> {
                cartDetailArrow.rotation = 0f
                if (cartDetailList.visibility == View.GONE) {
                    showCartDetail()

                } else if (cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            }
            R.id.right_btn -> {
                //清空购物车
                requestCartViewModel.clearCarts()
            }
            R.id.cart_invalid_view ->{
                if (cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            }
            R.id.layoutSearch ->{
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

    /**
     * 隐藏购物车详情
     */
    private fun hideCartDetail() {
        val hidden = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f
        )
        hidden.duration = ANIMATION_DURATION
        cartDetailList.startAnimation(hidden)
        cartDetailList.visibility = View.GONE
        cartDetailArrow.rotation = 0f
    }

    /**
     * 显示购物车详情
     */
    private fun showCartDetail() {
        val show = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        show.duration = ANIMATION_DURATION
        cartDetailList.startAnimation(show)
        cartDetailList.visibility = View.VISIBLE
        cartDetailArrow.rotation = -90f
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