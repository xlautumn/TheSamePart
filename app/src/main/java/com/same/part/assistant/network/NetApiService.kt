package com.same.part.assistant.network

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.RequestShopUserLogin
import com.same.part.assistant.model.ShopUserLoginModel
import retrofit2.http.*

/**
 * 网络API
 */
interface NetApiService {

    companion object {
        const val SERVER_URL = "http://118.31.54.197:8061/"
    }

    /**
     * 登录
     */
    @POST("shopUserLogin")
    suspend fun login(@Body requestShopUserLogin: RequestShopUserLogin): ApiResponse<ShopUserLoginModel>

}