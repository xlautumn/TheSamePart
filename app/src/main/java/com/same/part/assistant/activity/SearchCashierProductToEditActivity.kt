package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.same.part.assistant.R
import com.same.part.assistant.fragment.CashierFragmentV2
import com.same.part.assistant.viewmodel.state.SearchCashierProductToEditViewModel

/**
 * 搜索收银商品(编辑商品)
 */
class SearchCashierProductToEditActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier_product_to_edit)
    }

    override fun onBackPressed() {
        val viewModel = SearchCashierProductToEditViewModel.get(this)
        if (viewModel.changeCashierProductList.isNotEmpty()) {
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(
                    CashierFragmentV2.KEY_CASHIER_PRODUCT_CHANGE_LIST,
                    viewModel.changeCashierProductList
                )
            )
        }
        super.onBackPressed()
    }
}