package com.same.part.assistant.data.repository.request

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.model.RequestShopUserLogin
import com.same.part.assistant.app.network.NetworkApi
import com.same.part.assistant.data.model.RequestShopUserInfo
import com.same.part.assistant.data.model.ShopUserModel
import me.hgj.jetpackmvvm.network.AppException

/**
 * 从网络中获取数据
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
    suspend fun login(phoneNum: String, password: String): ApiResponse<ShopUserModel> {
        val loginData = NetworkApi.service.login(RequestShopUserLogin(phoneNum, password, true))
        if (loginData.isSucces()) {
            return getUserInfo(loginData.data.accessToken)
        } else {
            //抛出错误异常
            throw AppException(loginData.code, loginData.errorMsg)
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(token:String): ApiResponse<ShopUserModel> {
        return NetworkApi.service.getUserInfo(RequestShopUserInfo(token))
    }



}