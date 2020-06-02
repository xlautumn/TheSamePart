package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultState

class RequestCartViewModel(application: Application) : BaseViewModel(application) {

    private val _shopProductMap: MutableMap<String, ShopProduct> = mutableMapOf()

    /**
     * 购物车列表
     */
    private val _shopProductList = MutableLiveData<ArrayList<ShopProduct>>()
    val shopProductList: LiveData<ArrayList<ShopProduct>> = _shopProductList

    /**
     * 创建购物车请求结果
     */
    private val _createCartResult = MutableLiveData<ResultState<String>>()
    val createCartResult: LiveData<ResultState<String>> = _createCartResult

    /**
     * 获取购物车列表请求结果
     */
    private val _cartListResult = MutableLiveData<ResultState<String>>()
    val cartListResult: LiveData<ResultState<String>> = _cartListResult

    /**
     * 修改购物车数量请求结果
     */
    private val _updateCartResult = MutableLiveData<ResultState<String>>()
    val updateCartResult: LiveData<ResultState<String>> = _updateCartResult

    /**
     * 删除购物车请求结果
     */
    private val _delCartResult = MutableLiveData<ResultState<String>>()
    val delCartResult: LiveData<ResultState<String>> = _delCartResult

    /**
     * 创建订单请求结果
     */
    private val _createOrderResult = MutableLiveData<ResultState<String>>()
    val createOrderResult: LiveData<ResultState<String>> = _createOrderResult

    /**
     * 下单到支付或者微信请求结果
     */
    private val _paySignResult = MutableLiveData<ResultState<String>>()
    val paySignResult: LiveData<ResultState<String>> = _paySignResult


    /**
     * 添加购物车
     */
    fun addProduct(product: ProductDetailData) {
        if (_shopProductMap.containsKey(product.productId)) {
            _shopProductMap[product.productId]?.apply {
                updateCart(this, this.num + 1)
            }
        } else {
            createCart(product)
        }

    }

    /**
     * 减少购物车数量
     */
    fun minusProduct(product: ProductDetailData) {
        _shopProductMap[product.productId]?.apply {
            if (this.num > 1) {
                updateCart(this, this.num - 1)
            } else {
                delCarts(this)
            }
        }
    }


    private fun createCart(product: ProductDetailData) {
        val requestCreateCart = RequestCreateCart(
            productId = product.productId,
            properties = "",
            productSkuNumber = "",
            quantity = 1
        )
        requestResponseBody({ HttpRequestManger.instance.createCart(requestCreateCart) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                jsonObject.getJSONObject("content")?.apply {
                    val cartId = this.getString("cartId")
                    val shopProduct = ShopProduct(product, 1, cartId)
                    _shopProductMap[product.productId] = shopProduct
                    _shopProductList.value?.add(shopProduct)
                    _shopProductList.value = _shopProductList.value
                }

            }, error = {
                ToastUtils.showShort(it.errorMsg)
            })
    }

    private fun updateCart(shopProduct: ShopProduct, num: Int) {
        requestResponseBody(
            { HttpRequestManger.instance.updateCart(shopProduct.cartId, num) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                jsonObject.getJSONObject("content")?.apply {
                    val quantity = this.getString("quantity")
                    shopProduct.num = quantity.toInt()
                    _shopProductList.value = _shopProductList.value
                }

            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            }
        )
    }

    private fun delCarts(shopProduct: ShopProduct) {
        requestResponseBody(
            {
                HttpRequestManger.instance.delCarts(cartIds = shopProduct.cartId)
            },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                if (jsonObject.getInteger("code") == 1) {
                    _shopProductMap.remove(shopProduct.productDetailData.productId)
                    _shopProductList.value?.remove(shopProduct)
                    _shopProductList.value = _shopProductList.value
                }

            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            }
        )
    }

    fun createOrder(
        addressId: String,
        cartIds: String,
        category: String,
        orderRemarks: List<RequestCreateOrder.OrderRemark>,
        userId: String
    ) {
        requestResponseBody(
            {
                HttpRequestManger.instance.createOrder(
                    addressId,
                    cartIds,
                    category,
                    orderRemarks,
                    userId
                )
            },
            success = {},
            error = {}
        )
    }

    fun getPaySign(
        productOrderId: String,
        requestPay: RequestPay
    ) {
        request(
            {
                HttpRequestManger.instance.getPaySign(productOrderId, requestPay)
            },
            _createCartResult
        )
    }

    fun getCartList() {
        request(
            {
                HttpRequestManger.instance.getCartList()
            },
            _createCartResult
        )
    }


}