package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultState

class RequestCartViewModel(application: Application) : BaseViewModel(application) {

    private val _cartProductMap: MutableMap<String, CartProduct> = mutableMapOf()

    /**
     * 购物车列表
     */
    private val _cartProductList = MutableLiveData<ArrayList<CartProduct>>()
    val cartProductList: LiveData<ArrayList<CartProduct>> = _cartProductList

    /**
     * 获取购物车列表请求结果
     */
    private val _cartListResult = MutableLiveData<ResultState<String>>()
    val cartListResult: LiveData<ResultState<String>> = _cartListResult

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
    fun addShopProduct(shopProduct: ShopProduct) {
        val productId = shopProduct.productDetailData.productId
        if (_cartProductMap.containsKey(productId)) {
            _cartProductMap[productId]?.apply {
                updateCart(this, this.shopProduct.num + 1)
            }
        } else {
            createCart(shopProduct)
        }

    }

    /**
     * 减少购物车数量
     */
    fun minusShopProduct(shopProduct: ShopProduct) {
        _cartProductMap[shopProduct.getProductKey()]?.apply {
            if (this.shopProduct.num > 1) {
                updateCart(this, this.shopProduct.num - 1)
            } else {
                delCarts(this)
            }
        }
    }


    private fun createCart(shopProduct: ShopProduct) {
        val requestCreateCart = RequestCreateCart(
            productId = shopProduct.productDetailData.productId,
            properties = shopProduct.properties,
            productSkuNumber = shopProduct.productSkuNumber,
            quantity = shopProduct.num
        )
        requestResponseBody({ HttpRequestManger.instance.createCart(requestCreateCart) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                jsonObject.getJSONObject("content")?.apply {
                    val cartId = this.getString("cartId")
                    val cartProduct = CartProduct(shopProduct, cartId)
                    _cartProductMap[shopProduct.getProductKey()] = cartProduct
                    _cartProductList.value?.add(cartProduct)
                    _cartProductList.value = _cartProductList.value
                }

            }, error = {
                ToastUtils.showShort(it.errorMsg)
            })
    }

    private fun updateCart(cartProduct: CartProduct, num: Int) {
        requestResponseBody(
            { HttpRequestManger.instance.updateCart(cartProduct.cartId, num) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                jsonObject.getJSONObject("content")?.apply {
                    val quantity = this.getString("quantity")
                    cartProduct.shopProduct.num = quantity.toInt()
                    _cartProductList.value = _cartProductList.value
                }

            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            }
        )
    }

    private fun delCarts(cartProduct: CartProduct) {
        requestResponseBody(
            {
                HttpRequestManger.instance.delCarts(cartIds = cartProduct.cartId)
            },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                if (jsonObject.getInteger("code") == 1) {
                    _cartProductMap.remove(cartProduct.shopProduct.getProductKey())
                    _cartProductList.value?.remove(cartProduct)
                    _cartProductList.value = _cartProductList.value
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
            _paySignResult
        )
    }

    fun getCartList() {
        request(
            {
                HttpRequestManger.instance.getCartList()
            },
            _cartListResult
        )
    }


}