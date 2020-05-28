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
import com.same.part.assistant.manager.GoodPurchaseDataManager
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
        GoodPurchaseDataManager.getInstance().syncGoodPurchaseData()
        statement.setOnClickListener(this)
        rootDetail.setOnClickListener(this)

        //一级分类及产品列表
        fristLevelList.apply {
            mFirstLevelAdapter = activity?.let { PurchaseFirstLevelAdapter(it) }
            setOnItemClickListener { parent, view, position, id ->
                Toast.makeText(activity, "一级分类：$position", Toast.LENGTH_LONG).show()
                //刷新产品数据列表

            }
            adapter = mFirstLevelAdapter
        }
        //二级分类
        secondLevelListView.apply {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
            mSecondLevelAdapter = activity?.let { PurchaseSecondLevelAdapter(it) }
            //给adapter设置监听获取当前选中得一级分类
            mSecondLevelAdapter?.setOnItemClickListener { position, firstLevelName ->
                Toast.makeText(activity, "二级分类：$position", Toast.LENGTH_LONG).show()
                //标记当前分类为选中，其他得为未选中
                //刷新二级列表及产品数据列表

            }
            adapter = mSecondLevelAdapter
        }
        productList.apply {
            mProductAdapter = activity?.let { PurchaseProductAdapter(it) }
            adapter = mProductAdapter
            mProductAdapter
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