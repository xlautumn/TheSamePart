package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.viewmodel.request.RequestCashierViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class SearchCashierProductToEditViewModel(application: Application):BaseViewModel(application) {
    /**
     * 状态发生改变的商品集合
     */
    val changeCashierProductList = arrayListOf<CashierProduct>()

     fun addChangeCashierProductList(cashierProduct: CashierProduct){
        changeCashierProductList.apply {
            add(cashierProduct)
            distinctBy { it.productId }
        }
    }

    companion object {
        fun get(activity: FragmentActivity): SearchCashierProductToEditViewModel {
            return ViewModelProvider(
                activity,
                ViewModelProvider.AndroidViewModelFactory(activity.application)
            ).get(SearchCashierProductToEditViewModel::class.java)
        }
    }
}