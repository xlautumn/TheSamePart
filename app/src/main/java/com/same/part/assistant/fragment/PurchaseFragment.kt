package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import com.same.part.assistant.R
import com.same.part.assistant.adapter.LinkedDoubleViewAdapter
import com.same.part.assistant.manager.GoodPurchaseDataManager
import kotlinx.android.synthetic.main.fragment_purchase.*

/**
 * 采购
 */
class PurchaseFragment : Fragment(), View.OnClickListener {
    companion object {
        const val ANIMATION_DURATION = 200L
    }

    private var mAdapter: LinkedDoubleViewAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_purchase, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GoodPurchaseDataManager.getInstance().syncGoodPurchaseData()
        statement.setOnClickListener(this)
        rootDetail.setOnClickListener(this)
        linkedView.apply {
            activity?.also { ctx ->
                mAdapter = LinkedDoubleViewAdapter(ctx)
                mAdapter?.setCartDataChangedListener {

                    changed {
                        //更新 底部钱数，添加得数量等数据
                    }

                    addToCartData { isAdd, goodModel ->
                        //更新 底部钱数，添加得数量等数据
                        var cartGoodModels = GoodPurchaseDataManager.getInstance().cartGoodModels
                        if (isAdded) {
                            if (!cartGoodModels.contains(goodModel)) {
                                cartGoodModels.add(goodModel)
                            }
                        } else {
                            if (cartGoodModels.contains(goodModel)) {
                                cartGoodModels.remove(goodModel)
                            }
                        }
                        updateCart(isAdded, goodModel.firstLevel)

                    }

                    modifyCartData {

                    }
                }
                mAdapter?.let {
                    setAdapter(it)
                }
            }
        }
    }

    private fun updateCart(added: Boolean, firstLevel: String) {
        var goodPurchaseModels = GoodPurchaseDataManager.getInstance().goodPurchaseModels
        for (i in goodPurchaseModels.indices) {
            var goodClassModel = goodPurchaseModels[i]
            if (firstLevel == goodClassModel.level) {
                var selectNum = goodClassModel.selectNum
                goodClassModel.selectNum = if (added) selectNum++ else selectNum--

                var leftView = linkedView.getLeftView()
                var childAt = leftView?.getChildAt(i)
                childAt?.apply {
                    //如果selectNum>0且未选中item设置显示数量角标，selectNum==0 不显示数量角标
                }
                break
            }
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