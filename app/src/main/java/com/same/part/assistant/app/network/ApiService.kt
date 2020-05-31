package com.same.part.assistant.app.network

import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.data.model.*
import okhttp3.ResponseBody
import retrofit2.http.*


/**
 * 网络API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "http:192.168.110.224:8061/"
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
    suspend fun updateShopIno(
        @Header("WSCX") token: String, @Path("shopId") shopId: String,
        @Body requestUpdateShopInfo: RequestUpdateShopInfo
    ): ResponseBody

    /**
     * 添加商品分类
     */
    @POST("admin/custom-category")
    suspend fun addShopCategory(
        @Header("WSCX") token: String,
        @Body requestShopCategory: RequestShopCategoryInfo
    ): ResponseBody


    /**
     * 编辑店铺商品分类
     */
    @PUT("admin/custom-category/{customCategoryId}")
    suspend fun editShopCategory(
        @Path("customCategoryId") customCategoryId: String,
        @Body requestShopCategory: RequestShopCategoryInfo
    ): ResponseBody


    /**
     * 查询商品分类详情
     */
    @GET("custom-category/{customCategoryId}")
    suspend fun queryShopCategoryDetail(
        @Header("WSCX") token: String,
        @Path("customCategoryId") customCategoryId: String,
        @Query("appKey") appkey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()): ResponseBody


    /**
     * 添加/更新收银商品
     */
    @PUT("amountCommodity/createOrUpdate")
    suspend fun createOrUpdateCashierGood(
        @Header("WSCX") token: String,
        @Body requestShopCategory: CreateOrUpdateGoodsInfo
    ): ResponseBody

    /**
     * 获取店铺商品分类列表
     */
    @GET("custom-categories")
    suspend fun getProductClassificationList(
        @Header("WSCX") token: String,
        @Query("name") name: String = "",
        @Query("page") page: String = "",
        @Query("size") size: String = "${Int.MAX_VALUE}",
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret(),
        @Query("shopId") shopId: String = CacheUtil.getShopId().toString()
    ): ResponseBody

    /**
     * 获取收银商品详情
     */
    @GET("amountCommodity/getDetail/{productId}")
    suspend fun getCashierGoodDetail(
        @Header("WSCX") token: String,
        @Path("productId") productId: String,
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ApiResponse<CashierDetailMode>

}