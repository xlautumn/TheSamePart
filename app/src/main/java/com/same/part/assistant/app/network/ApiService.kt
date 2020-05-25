package com.same.part.assistant.app.network

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.RequestShopUserLogin
import com.same.part.assistant.model.ShopUserLoginModel
import retrofit2.http.*

/**
 * 网络API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "https://tzyf.godoit.vip/easyapi/"
    }

    /**
     * 登录
     */
    @POST("shopUserLogin")
    suspend fun login(@Body requestShopUserLogin: RequestShopUserLogin): ApiResponse<ShopUserLoginModel>

}