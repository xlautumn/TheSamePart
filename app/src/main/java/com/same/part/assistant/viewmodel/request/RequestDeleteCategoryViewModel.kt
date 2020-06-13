package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestDeleteCategoryViewModel(application: Application) : BaseViewModel(application) {

    /**删除店铺商品分类结果*/
    var deleteShopCategoryResult = MutableLiveData<Int>()

    /**
     * 删除店铺商品分类
     */
    fun deleteShopCategory(categoryId: String,position:Int) {
        requestResponseBody(
            {
                HttpRequestManger.instance.deleteShopCategory(
                    categoryId
                )
            }, success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                val code = jsonObject.getIntValue("code")
                if (code == 1) {
                    ToastUtils.showLong(jsonObject.getString("message"))
                    deleteShopCategoryResult.postValue(position)
                } else {
                    ToastUtils.showLong(jsonObject.getString("msg"))
                }
            }, error = {
                ToastUtils.showLong(it.errorMsg)
            }, isShowDialog = true
        )
    }
}