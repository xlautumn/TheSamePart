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
    fun queryShopCategoryDetail(
        size: String,
        page: String//请求页码
    ) {
        val customCategoryId = currentSecondCategoryId.value
        if (!customCategoryId.isNullOrEmpty()) {
            requestTR(
                {
                    HttpRequestManger.instance.getProduct(
                        page = page,
                        size = size,
                        customCategoryId = customCategoryId,
                        sort = SORT_TYPE_DEFAULT
                    )
                },
                _productResultState,
                paresResult = {
                    if (it.isSucces()){
                        Triple(GetCashierCategoryDetail(customCategoryId,it.data), it.code, "")
                    }else{
                        Triple(null, it.code, it.errorMsg)
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

    /**
     * 批量删除收银商品
     */
    fun delCashierProduct(
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
        requestResponseBody({
            HttpRequestManger.instance.delCashierProduct(
                arrayListOf(
                    cashierProduct.productId
                )
            )
        },
            success = {
                parseResponseBody(it, onSuccess, onError)
            }, error = {
                onError(it.errorMsg)
            })
    }

    /**
     * @param list 商品数据发生变化的商品集合
     * @param notifyCurrentVisibleProductChange 通知当前正在显示的商品数据发生变化
     */
    fun updateProductBecauseOfSearch(
        list: List<CashierProduct>?,
        delList: List<CashierProduct>?,
        notifyCurrentVisibleProductChange: () -> Unit
    ) {
        if (list.isNullOrEmpty() && delList.isNullOrEmpty()) {
            return
        }
        val tempList = list?.let { ArrayList(it) } ?: arrayListOf()
        val tempDelList = delList?.let { ArrayList(it) } ?: arrayListOf()
        var needNotifyCurrentVisibleProductChange = false
        cashierAllProduct?.categoryProductMap?.entries?.forEach { entry ->
            val secondCategoryId = entry.key
            val iterator = tempList.iterator()
            while (iterator.hasNext()) {
                val changeItem = iterator.next()
                entry.value.find { cashierProduct -> cashierProduct.productId == changeItem.productId }
                    ?.let {
                        it.state = changeItem.state
                        iterator.remove()
                        if (secondCategoryId == currentSecondCategoryId.value) {
                            needNotifyCurrentVisibleProductChange = true
                        }
                    }
            }
            val delIterator = tempDelList.iterator()
            while (delIterator.hasNext()) {
                val delCashierProduct = delIterator.next()
                entry.value.find { cashierProduct ->
                    cashierProduct.productId == delCashierProduct.productId
                }?.let {
                    entry.value.remove(it)
                    delIterator.remove()
                    if (secondCategoryId == currentSecondCategoryId.value) {
                        needNotifyCurrentVisibleProductChange = true
                    }
                }
            }
        }
        if (needNotifyCurrentVisibleProductChange) {
            notifyCurrentVisibleProductChange()
        }
    }


    fun setCashierAllProduct(cashierAllProduct: CashierAllProduct) {
        this.cashierAllProduct = cashierAllProduct
    }

    fun getCashierAllProduct(): CashierAllProduct? {
        return this.cashierAllProduct
    }

    fun clearData(){
        this.cashierAllProduct = null
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
        const val SORT_TYPE_DEFAULT = "product.sequence,desc"//默认
        const val SORT_TYPE_TOTAL_SALES_DESC = "product.totalSales,desc"//总销量 降序
        const val SORT_TYPE_PRICE_DESC = "product.price,desc"//价格 降序
        const val SORT_TYPE_TIME_DESC = "product.addTime,desc"//添加时间 降序
    }

}