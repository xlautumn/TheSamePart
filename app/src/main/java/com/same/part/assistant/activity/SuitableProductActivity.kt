package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.same.part.assistant.R
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import me.hgj.jetpackmvvm.base.activity.BaseVmActivity

/**
 * 选择优惠券适用商品
 */
class SuitableProductActivity : BaseVmActivity<SuitableProductViewModel>() {
    override fun layoutId(): Int = R.layout.activity_suitable_product

    override fun initView(savedInstanceState: Bundle?) {
        val list = intent.getSerializableExtra(KEY_SUITABLE_PRODUCT_LIST) as?List<CashierProduct>
        if (!list.isNullOrEmpty()) {
            mViewModel.setSuitableProductList(list)
        }
    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

    override fun createObserver() {

    }

    override fun onBackPressed() {
        showMessage("请确认商品是否完成选择", positiveAction = {
            val list =
                mViewModel.suitableProductList.value?.let { ArrayList(it) }
                    ?: arrayListOf()
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(KEY_SUITABLE_PRODUCT_LIST, list)
            )
            finish()
        }, negativeButtonText = "取消")
//        super.onBackPressed()
    }

    companion object {
        /**
         * 商品集合的Key
         */
        const val KEY_SUITABLE_PRODUCT_LIST = "KEY_SUITABLE_PRODUCT_LIST"
    }

}