package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.same.part.assistant.R
import com.same.part.assistant.adapter.PurchaseSecondLevelAdapter
import com.same.part.assistant.adapter.PurchaseProductAdapter
import com.same.part.assistant.adapter.PurchaseFirstLevelAdapter
import com.same.part.assistant.data.model.CategoryData
import com.same.part.assistant.manager.GoodPurchaseManager
import kotlinx.android.synthetic.main.fragment_purchase.*

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
            mProductAdapter = activity?.let { PurchaseProductAdapter(it) }
            adapter = mProductAdapter
            mProductAdapter
        }
        GoodPurchaseManager.instance.syncPurchaseCategoryData {
            refreshCategory(0)
        }
        GoodPurchaseManager.instance.getCartProductList{

        }
    }

    private fun refreshSecondCategory(position: Int) {
        val categoryDataList = GoodPurchaseManager.instance.getPurchaseCategoryData()
        categoryDataList.takeIf { GoodPurchaseManager.instance.mCurrentCategoryFirstLevel in categoryDataList.indices }
            ?.let { dataList ->
                val categoryData =
                    dataList[GoodPurchaseManager.instance.mCurrentCategoryFirstLevel]
                categoryData.let { data ->
                    data.sons?.takeIf { position in it.indices }?.apply {
                        for (i in 0 until size) this[i].let {
                            it.isSelected = (i == position)
                        }
                        mSecondLevelAdapter?.setData(this)
                        //请求产品列表
                        refreshProduct(get(position))
                    }
                }
            }
    }

    private fun refreshCategory(position: Int) {
        GoodPurchaseManager.instance.mCurrentCategoryFirstLevel = position
        val categoryDataList = GoodPurchaseManager.instance.getPurchaseCategoryData()
        for (i in 0 until categoryDataList.size) categoryDataList[i].let {
            it.isSelected = (i == position)
        }
        mFirstLevelAdapter?.setData(categoryDataList)
        categoryDataList.takeIf { position in categoryDataList.indices }?.let {
            val sons = categoryDataList[position].sons
            sons?.apply {
                for (i in 0 until size) this[i].let {
                    it.isSelected = (i == 0)
                }
                mSecondLevelAdapter?.setData(this)

                refreshProduct(get(0))
            }
        }
    }

    private fun refreshProduct(data: CategoryData) {
        GoodPurchaseManager.instance.syncPurchaseProductData(
            data.productCategoryId,
            data.name
        ) {
            //刷新产品数据
            val productData =
                GoodPurchaseManager.instance.getPurchaseProductData()
            mProductAdapter?.setData(productData)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.statement -> {
                //TODO 结算
            }
            R.id.rootDetail -> {
                cartDetailArrow.rotation = 0f
                if (cartDetailList.visibility == View.GONE) {
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

                } else if (cartDetailList.visibility == View.VISIBLE) {
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
            }
        }
    }
}