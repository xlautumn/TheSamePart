package com.same.part.assistant.data.repository.request

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.RequestShopUserLogin
import com.same.part.assistant.model.ShopUserLoginModel
import com.same.part.assistant.app.network.NetworkApi

/**
 * 作者　: hegaojian
 * 时间　: 2020/5/4
 * 描述　: 从网络中获取数据
 */
class HttpRequestManger {

    companion object {
        val instance: HttpRequestManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpRequestManger()
        }
    }

    /**
     * 登陆
     */
    suspend fun login(phoneNum: String, password: String): ApiResponse<ShopUserLoginModel> {
        return NetworkApi.service.login(RequestShopUserLogin(phoneNum,password,true))
    }
}