package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierDetailMode
import com.same.part.assistant.data.model.CreateOrUpdateGoodsInfo
import com.same.part.assistant.data.model.ProductClassificationModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultResponseBodyState
import me.hgj.jetpackmvvm.state.ResultState

class RequestAddCashierGoodViewModel(application: Application) : BaseViewModel(application) {

    var createOrUpdateCashierGoodsResult = MutableLiveData<ResultResponseBodyState>()

    var productClassificationListResult: MutableLiveData<ArrayList<ProductClassificationModel>> =
        MutableLiveData()

    var classificationnameListResult: MutableLiveData<ArrayList<String>> = MutableLiveData()

    var cashierGoodsDetailInfoResult = MutableLiveData<ResultState<CashierDetailMode>>()

    /**
     * 添加/更新收银商品
     */
    fun createOrUpdateCashierGood(createOrUpdateGoodsInfo: CreateOrUpdateGoodsInfo) {
        requestResponseBody(
            {
                HttpRequestManger.instance.createOrUpdateCashierGood(
                    CacheUtil.getToken(),
                    createOrUpdateGoodsInfo
                )
            },
            createOrUpdateCashierGoodsResult
        )
    }

    /**
     * 编辑时需查询商品分类和详情，添加时只查商品分类
     */
    fun queryCashierGood(isEditPage: Boolean,productId: String) {
        //查商品分类
        requestResponseBody(
            { HttpRequestManger.instance.getProductClassificationList() },
            { responsebody ->
                try {
                    val response: String = responsebody.string()
                    val jsonObject = JSON.parseObject(response)
                    val nameList = ArrayList<String>()
                    val itemList = ArrayList<ProductClassificationModel>()
                    jsonObject.getJSONArray("content").takeIf { it.size >= 0 }?.apply {
                        for (i in 0 until size) {
                            getJSONObject(i)?.apply {
                                val id = getString("customCategoryId")
                                val name = getString("name")
                                val parentId =
                                    getJSONObject("parent")?.getString("customCategoryId").orEmpty()
                                //过滤掉一级分类
                                if (parentId.isNotEmpty()) {
                                    ProductClassificationModel(id, name, parentId).apply {
                                        itemList.add(this)
                                    }
                                    nameList.add(name)
                                }
                            }
                        }
                    }
                    classificationnameListResult.postValue(nameList)
                    productClassificationListResult.postValue(itemList)
                    //编辑还需查询详情信息
                    if (itemList.size > 0 && isEditPage) {
                        getCashierGoodDetail(productId)
                    }
                } catch (e: Exception) {

                }
            })
    }

    /**
     * 收银商品详情
     */
    fun getCashierGoodDetail(productId: String) {
        request(
            {
                HttpRequestManger.instance.getCashierGoodDetail(
                    productId)
            }
            , cashierGoodsDetailInfoResult
        )
    }

}