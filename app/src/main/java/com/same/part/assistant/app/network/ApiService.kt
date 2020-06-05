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
        //生产
//        const val SERVER_URL = "https://product.tfsq.vip/easyapi/"
        //测试
        const val SERVER_URL = "https://test.tfsq.vip/easyapi/"
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
     * 获取用户地址
     */
    @GET("wscx/getAddressesByUserid")
    suspend fun getAddressesByUserid(
        @Header("WSCX") token: String,
        @Query("userId") userId: String
    ): ApiResponse<AddressMsg>
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
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody


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


    /**
     * 获取店铺优惠劵活动列表
     */
    @GET("coupon-activities")
    suspend fun getCouponsList(
        @Query("shopId") shopId: String = CacheUtil.getShopId()?.toString() ?: "",
        @Query("ifPlatform") ifPlatform: Boolean = false,
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody


    /**
     * 创建优惠劵活动
     */
    @POST("biz/coupon-activity")
    suspend fun createCouponActivity(
        @Body requestCreateCouponInfo: RequestCreateCouponInfo
    ): ResponseBody

    /**
     * 获取购物车列表
     */
    @GET("carts/group-by-shop")
    suspend fun getCartList(
        @Query("appKey") appKey: String,
        @Query("appSecret") appSecret: String,
        @Query("userId") userId: Int,
        @Query("shopId") shopId: Int
    ): ResponseBody

    /**
     * 创建购物车
     */
    @POST("cart")
    suspend fun createCart(@Body requestCreateCart: RequestCreateCart): ResponseBody

    /**
     * 修改购物车数量
     */
    @PUT("cart/{cartId}/quantity")
    suspend fun updateCart(
        @Path("cartId") cartId: String,
        @Body requestUpdateCart: RequestUpdateCart
    ): ResponseBody

    /**
     * 批量删除购物车
     */
    @DELETE("carts/{cartIds}")
    suspend fun delCarts(
        @Path("cartIds") cartIds: String,
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody

    /**
     * 创建商品订单
     */
    @POST("cart/product-order")
    suspend fun createOrder(@Body requestCreateOrder: RequestCreateOrder): ResponseBody

    /**
     * 下单到支付或者微信
     */
    @PUT("product-order/{productOrderId}/mobile-pay")
    suspend fun getPaySign(
        @Path("productOrderId") productOrderId: String,
        @Body requestPay: RequestPay
    ): ResponseBody


    /**
     * 获取店铺账号列表
     */
    @GET("shopUser/getShopUser/{shopId}")
    suspend fun getShopAccounts(
        @Header("WSCX") token: String = CacheUtil.getToken(),
        @Path("shopId") shopId: Int = CacheUtil.getShopId() ?: 0
    ): ResponseBody

    /**
     * 修改账号密码
     */
    @POST("shopUser/updateShopUser")
    suspend fun changeAccountPwd(
        @Body changePwdInfo: ChangePwdInfo,
        @Header("WSCX") token: String = CacheUtil.getToken()
    ): ApiResponse<ChangePwdModel>


    /**
     * 获取店铺会员卡列表
     */
    @GET("cards")
    suspend fun getMemberCardList(
        @Query("page") page: Int,
        @Query("size") size: Int = 10,
        @Query("shopId") shopId: String = CacheUtil.getShopId()?.toString() ?: "",
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody


    /**
     * 创建店铺会员卡
     */
    @POST("biz/card")
    suspend fun createMemberCard(
        @Body createMemberCard: CreateMemberCard
    ): ResponseBody


    /**
     * 编辑店铺会员卡
     */
    @PUT("biz/card/{cardId}")
    suspend fun editMemberCard(
        @Path("cardId") cardId: String,
        @Body createMemberCard: CreateMemberCard
    ): ResponseBody


    /**
     * 获取店铺客户列表
     */
    @GET("biz/customers")
    suspend fun getCustomerList(
        @Query("shopId") shopId: String = CacheUtil.getShopId()?.toString() ?: "",
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody

}