package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestSearchResultViewModel(application: Application) : BaseViewModel(application) {

    private val _searchResultList = MutableLiveData<ArrayList<ProductDetailData>>()
    val searchResultList: LiveData<ArrayList<ProductDetailData>> = _searchResultList

    init {
        _searchResultList.value = arrayListOf()
    }

    /**
     * 请求搜索结果
     */
    fun requestSearchResultList(name: String) {
        requestResponseBody(
            {
                HttpRequestManger.instance.getSearchResult(name)
            },
            success = {
                val result = it.string()
                val resultObject = JSON.parse(result)
                if (resultObject is JSONObject) {
                    //数据解析
                    val resultList = ArrayList<ProductDetailData>()
                    resultObject.getJSONArray("content")?.takeIf { it.size > 0 }?.let { jsonArray ->
                        for (i in 0 until jsonArray.size) {
                            jsonArray.getJSONObject(i)?.let { jsonObject ->
                                ProductDetailData().apply {
                                    this.productId = jsonObject.getString("productId") ?: ""
                                    this.name = jsonObject.getString("name")
                                    this.price = jsonObject.getString("price")
                                    this.img = jsonObject.getString("img")
                                    val productSku = jsonObject.getJSONArray("productSku")
                                    this.hasSku = productSku?.size != 0 ?: false
                                    resultList.add(this)
                                }
                            }
                        }
                    }
                    _searchResultList.postValue(resultList)
                }
            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            },
            isShowDialog = true
        )
    }

    fun clear() {
        _searchResultList.value = arrayListOf()
    }
}