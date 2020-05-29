package com.same.part.assistant.app.network

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.model.*
import okhttp3.ResponseBody
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

    /**
     * 账号信息
     */
    @POST("getAccountByToken")
    suspend fun getUserInfo(@Body requestShopUserInfo: RequestShopUserInfo): ApiResponse<ShopUserModel>

    /**
     * 获取店铺信息
     */
    @GET("wscx/shop")
    suspend fun getShopInfo(@Header("WSCX") token: String): ApiResponse<ShopModel>


    /**
     * 获取七牛key
     */
    @GET("qiniu/upToken")
    suspend fun getQiniuToken(@Header("WSCX") token: String): ResponseBody

    /**
     * 更新店铺信息
     */
    @PUT("wscx/updateShop/{shopId}")
    suspend fun updateShopIno(@Header("WSCX") token: String, @Path("shopId") shopId: String,
                              @Body requestUpdateShopInfo: RequestUpdateShopInfo):ResponseBody


}