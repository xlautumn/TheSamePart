package com.same.part.assistant.viewmodel.request

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.utils.CalculateUtil
import com.same.part.assistant.utils.Util
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

    init {
        _cartProductList.value = arrayListOf()
    }

    /**
     * 获取购物车id字符串
     */
    private val cartIds
        get() = _cartProductList.value?.let { it.joinToString(separator = ",") { it.cartId } } ?: ""

    val totalPrice
        get() = _cartProductList.value?.fold(
            "0.00",
            fun(acc: String, cartProduct: CartProduct): String =
                CalculateUtil.add(
                    acc,
                    CalculateUtil.multiply(
                        cartProduct.price,
                        cartProduct.shopProduct.num.toString()
                    )
                )
        ).let { Util.format2(it) }

    val totalNum
        get() = _cartProductList.value?.fold(0,
            { acc: Int, cartProduct: CartProduct -> acc + cartProduct.shopProduct.num })

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
        val productKey = shopProduct.getProductKey()
        if (_cartProductMap.containsKey(productKey)) {
            _cartProductMap[productKey]?.apply {
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
                delCart(this)
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
                    val price = this.getString("price")
                    val skuProperties = this.getString("skuProperties")
                    val cartProduct = CartProduct(shopProduct, cartId, price,skuProperties)
                    cartProduct.shopProduct.productDetailData.cartNum = shopProduct.num
                    _cartProductMap[shopProduct.getProductKey()] = cartProduct
                    _cartProductList.value?.add(cartProduct)
                    _cartProductList.value = _cartProductList.value
                }

            }, error = {
                ToastUtils.showShort(it.errorMsg)
            })
    }

    /**
     * 更新购物车
     */
    private fun updateCart(cartProduct: CartProduct, num: Int) {
        requestResponseBody(
            { HttpRequestManger.instance.updateCart(cartProduct.cartId, num) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                jsonObject.getJSONObject("content")?.apply {
                    val quantity = this.getString("quantity")
                    val price = getString("price")
                    val num = quantity.toFloat().toInt()
                    cartProduct.shopProduct.productDetailData.cartNum = num
                    cartProduct.shopProduct.num = num
                    cartProduct.price = price
                    _cartProductList.value = _cartProductList.value
                }

            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            }
        )
    }

    /**
     * 删除购物车
     */
    private fun delCart(cartProduct: CartProduct) {
        requestResponseBody(
            {
                HttpRequestManger.instance.delCarts(cartIds = cartProduct.cartId)
            },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                if (jsonObject.getInteger("code") == 1) {
                    cartProduct.shopProduct.productDetailData.cartNum = 0
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

    /**
     * 清空购物车
     */
    fun clearCarts() {

        if (TextUtils.isEmpty(cartIds)) {
            return
        }
        requestResponseBody(
            {
                HttpRequestManger.instance.delCarts(cartIds = cartIds)
            },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                if (jsonObject.getInteger("code") == 1) {
                    _cartProductMap.clear()
                    _cartProductList.value = arrayListOf()
                }

            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            }
        )
    }

    /**
     * 创建订单的
     */
    fun createOrder(
        addressId: String,
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

    /**
     * 请求购物车列表
     */
    fun getCartList() {
        requestResponseBody(
            {
                HttpRequestManger.instance.getCartList()
            },
            success = {
                val result = it.string()

                val resultObject = JSON.parse(result)

                if (resultObject is JSONObject) {
                    val code = resultObject.getString("code")
                    if (TextUtils.equals("0", code)) {
                        //暂无数据
                        _cartProductList.postValue(_cartProductList.value)
                    } else {
                        val message = resultObject.getString("message")
                        ToastUtils.showShort(message)
                    }
                } else if (resultObject is JSONArray) {
                    val getCartListResponse =
                        GsonUtils.fromJson(result, GetCartListResponse::class.java)

                    getCartListResponse.takeUnless { it.isNullOrEmpty() }?.get(0)?.carts?.map {

                        val productDetailSkuList =
                            it.product.productSku.map {
                                ProductDetailSku(
                                    it.productSkuId,
                                    it.weight
                                )
                            }
                                .let { ArrayList(it) }
                        val product = it.product
                        val productDetailData = ProductDetailData(
                            product.productId,
                            product.name,
                            product.price,
                            product.img,
                            productDetailSkuList
                        )
                        CartProduct(
                            ShopProduct(
                                productDetailData,
                                it.quantity.toFloat().toInt(),
                                it.productSku.number,
                                it.properties
                            ),
                            it.cartId,
                            it.price,
                            it.skuProperties
                        )
                    }?.let { cartList ->
                        cartList.distinctBy { cartProduct -> cartProduct.shopProduct.getProductKey() }
                            .forEach { cartProduct ->
                                _cartProductMap[cartProduct.shopProduct.getProductKey()] =
                                    cartProduct
                            }
                        _cartProductList.value?.addAll(cartList)
                        _cartProductList.postValue(_cartProductList.value)
                    }
                }
            },

            error = {
                ToastUtils.showShort(it.errorMsg)
            }

        )
    }


}