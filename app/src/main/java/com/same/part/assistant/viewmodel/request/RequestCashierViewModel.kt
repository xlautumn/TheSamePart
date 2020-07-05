package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.GsonUtils
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.ext.requestTR
import me.hgj.jetpackmvvm.state.ResultState
import okhttp3.ResponseBody

/**
 * 收银商品
 */
class RequestCashierViewModel(application: Application) : BaseViewModel(application) {

    private var cashierAllProduct: CashierAllProduct? = null

    private var _resultState = MutableLiveData<ResultState<List<CustomCategory>>>()
    val resultState: LiveData<ResultState<List<CustomCategory>>> get() = _resultState

    private var _productResultState = MutableLiveData<ResultState<GetCashierCategoryDetail>>()
    val productResultState: LiveData<ResultState<GetCashierCategoryDetail>> get() = _productResultState

    /**
     * 一级分类Id
     */
    val firstCategoryId = StringLiveData("")

    val currentSecondCategoryId = StringLiveData("")


    /**
     * 请求收银商品所有分类
     */
    fun requestCashierClassification() {
        request(
            {
                HttpRequestManger.instance.getAllCustomCategoryList(
                    CacheUtil.getShopId()?.toString() ?: ""
                )
            },
            _resultState,
            true
        )
    }

    /**
     * 查询商品分类详情
     */
    fun queryShopCategoryDetail() {
        val customCategoryId = currentSecondCategoryId.value
        if (!customCategoryId.isNullOrEmpty()) {
            requestTR(
                {
                    HttpRequestManger.instance.queryShopCategoryDetail(
                        token = CacheUtil.getToken(),
                        customCategoryId = customCategoryId
                    )
                },
                _productResultState,
                paresResult = {
                    val response: String = it.string()
                    val responseObject = JSON.parseObject(response)
                    val code = responseObject.getString("code")
                    if (code == "1") {
                        val content = responseObject.getString("content")
                        val getCashierCategoryDetail =
                            GsonUtils.fromJson(content, GetCashierCategoryDetail::class.java)
                        Triple(getCashierCategoryDetail, code, "")
                    } else {
                        val msg = responseObject.getString("message")
                        Triple(null, code, msg)
                    }

                },
                isShowDialog = true
            )
        }
    }

    /**
     * 更新商品上下架状态
     */
    fun updateProduct(
        cashierProduct: CashierProduct,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
         fun parseResponseBody(
            it: ResponseBody,
            onSuccess: (String) -> Unit,
            onError: (String) -> Unit
        ) {
            val response: String = it.string()
            val responseObject = JSON.parseObject(response)
            val code = responseObject.getInteger("code")
            val msg = responseObject.getString("message")
            if (code == 1) {
                onSuccess(msg)
            } else {
                onError(msg)
            }
        }
        if (cashierProduct.state == "1") {
            requestResponseBody({ HttpRequestManger.instance.undercarriageProduct(cashierProduct.productId) },
                success = {
                    parseResponseBody(it, onSuccess, onError)
                }, error = {
                    onError(it.errorMsg)
                })
        } else {
            requestResponseBody({ HttpRequestManger.instance.putawayProduct(cashierProduct.productId) },
                success = {
                    parseResponseBody(it, onSuccess, onError)
                }, error = {
                    onError(it.errorMsg)
                })
        }
    }




    fun setCashierAllProduct(cashierAllProduct: CashierAllProduct) {
        this.cashierAllProduct = cashierAllProduct
    }

    fun getCashierAllProduct(): CashierAllProduct? {
        return this.cashierAllProduct
    }

    /**
     * 获取分类名称
     */
    fun getCategoryName(position: Int): String =
        this.cashierAllProduct?.getFirstCategory(position)?.name ?: ""

    fun getSecondTitleList(firstCategoryId: String) =
        this.cashierAllProduct?.getSecondTitleList(firstCategoryId) ?: arrayListOf()

    /**
     * 获取分类的产品集合
     */
    fun getProductList(secondCategoryId: String): ArrayList<CashierProduct> =
        this.cashierAllProduct?.getProductList(secondCategoryId) ?: arrayListOf()

    companion object {

        fun get(activity: FragmentActivity): RequestCashierViewModel {
            return ViewModelProvider(
                activity,
                ViewModelProvider.AndroidViewModelFactory(activity.application)
            ).get(RequestCashierViewModel::class.java)
        }
    }
}