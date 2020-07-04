package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState

class SuitableProductViewModel(application: Application) : BaseViewModel(application) {

    /**
     * 适用商品集合
     */
    private val _suitableProductList = MutableLiveData<ArrayList<CashierProduct>>()

    val suitableProductList: LiveData<ArrayList<CashierProduct>> = _suitableProductList


    /**
     * 收银商品
     */
    val cashierProductResultState = MutableLiveData<ResultState<GetCashierProductMsg>>()
    private val _cashierProductList = MutableLiveData<ArrayList<CashierProduct>>()
    val cashierProductList: LiveData<ArrayList<CashierProduct>> = _cashierProductList


    fun setSuitableProductList(data: List<CashierProduct>){
        _suitableProductList.value = ArrayList(data)
    }


    fun setProductList(data:List<CashierProduct>,isRefresh:Boolean ){
        _cashierProductList.value?.apply {
            if (isRefresh){
                this.clear()
                this.addAll(data)
            }else{
                this.addAll(data)
            }
            _cashierProductList.value = _cashierProductList.value
        }?: kotlin.run {
            val list = ArrayList(data)
            _cashierProductList.value = list
        }
    }

    /**
     * 当前商品是否已添加到适用商品集合
     */
    fun hasAdd(product: CashierProduct): Boolean {
        return suitableProductList.value?.find { it.productId == product.productId }?.let { true }
            ?: false
    }

    /**
     * 添加商品到适用商品集合
     */
    fun addProductList(list: List<CashierProduct>) {
        if (list.isNotEmpty()) {
            if (_suitableProductList.value == null) {
                _suitableProductList.value = arrayListOf()
            }
            _suitableProductList.value?.apply {
                addAll(list)
                distinctBy { it.productId }
            }
            _suitableProductList.value = _suitableProductList.value
        }
    }

    /**
     * 从适用商品集合这个删除商品
     */
    fun delProduct(product: CashierProduct) {
        _suitableProductList.value?.remove(product)
        _suitableProductList.value = _suitableProductList.value
    }

    /**
     * 获取收银商品列表
     */
    fun getCashierProductList(page: Int, size: Int) {
        request(
            { HttpRequestManger.instance.getCashierProductList(page, size) },
            cashierProductResultState, true
        )
    }
}