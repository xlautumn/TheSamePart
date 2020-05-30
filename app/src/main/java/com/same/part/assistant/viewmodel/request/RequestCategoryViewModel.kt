package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.RequestShopCategoryInfo
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultResponseBodyState

class RequestCategoryViewModel(application: Application) : BaseViewModel(application) {

    var queryShopCategoryDetailResult = MutableLiveData<ResultResponseBodyState>()

    var addShopCategoryResult = MutableLiveData<ResultResponseBodyState>()

    var editShopCategoryResult = MutableLiveData<ResultResponseBodyState>()

    /**
     * 查询商品分类详情
     */
    fun queryShopCategoryDetail(token: String, customCategoryId: String) {
        requestResponseBody(
            { HttpRequestManger.instance.queryShopCategoryDetail(token, customCategoryId) },
            queryShopCategoryDetailResult
        )
    }

    /**
     * 添加商品分类
     */
    fun addShopCategory(token: String, requestShopCategory: RequestShopCategoryInfo) {
        requestResponseBody(
            { HttpRequestManger.instance.addShopCategory(token, requestShopCategory) },
            addShopCategoryResult
        )
    }

    /**
     * 编辑店铺商品分类
     */
    fun editShopCategory( customCategoryId: String, requestShopCategory: RequestShopCategoryInfo) {
        requestResponseBody(
            { HttpRequestManger.instance.editShopCategory(customCategoryId, requestShopCategory) },
            editShopCategoryResult
        )
    }


}