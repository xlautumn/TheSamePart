package com.same.part.assistant.app.network

import com.same.part.assistant.BuildConfig
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
        private const val PRODUCT_SERVER_URL = "https://product.tfsq.vip/easyapi/"

        //        测试
        private const val TEST_SERVER_URL = "https://test.tfsq.vip/easyapi/"
        val SERVER_URL = if (BuildConfig.IS_TEST_URL) {
            TEST_SERVER_URL
        } else {
            PRODUCT_SERVER_URL
        }
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

    /**
     * 确认收货
     */
    @PUT("product-order/{orderId}/conform")
    suspend fun conformDelivery(
        @Path("orderId") orderId: String,
        @Body requestConformDelivery: RequestConformDelivery = RequestConformDelivery()
    ): ResponseBody

    /**
     * 删除店铺商品分类
     */
    @DELETE("admin/custom-category/{categoryId}")
    suspend fun deleteShopCategory(
        @Path("categoryId") categoryId: String,
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody

    /**
     * 获取搜索结果
     */
    @GET("products")
    suspend fun getSearchResult(
        @Query("name") name: String,
        @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret(),
        @Query("shopId") shopId: Int = 2000,
        @Query("state") state: Int = 1
    ): ResponseBody

    /**
     * 创建商品
     */
    @POST("product/createProduct")
    suspend fun createProduct(@Body requestCreateProduct: RequestCreateProduct): ResponseBody

    /**
     * 编辑商品信息
     */
    @PUT("product/updateProduct/{productId}")
    suspend fun updateProduct(
        @Path("productId") productId: String,
        @Body requestCreateProduct: RequestCreateProduct
    ): ResponseBody

    /**
     * 删除店铺会员卡
     */
    @DELETE("biz/card/{cardId}")
    suspend fun delVIPCard(
        @Path("cardId") cardId: Int, @Query("appKey") appKey: String = CacheUtil.getAppKey(),
        @Query("appSecret") appSecret: String = CacheUtil.getAppSecret()
    ): ResponseBody

    /**
     * 获取商品全部分类
     */
    @GET("wscx/getAllCustomCategoryList")
    suspend fun getAllCustomCategoryList(
        @Header("WSCX") token: String,
        @Query("shopId") shopId: String
    ): ApiResponse<List<CustomCategory>>

    /**
     * 会员客户列表
     */
    @GET("wx/getVipCustomer")
    suspend fun getMemberCustomer(
        @Header("WSCX") token: String,
        @Query("shopId") shopId: String
    ): ApiResponse<List<Customer>>

    /**
     * 发放优惠券
     */
    @POST("biz/coupon-activity/send")
    suspend fun sendCoupon(@Body requestSendCoupon: RequestSendCoupon): ResponseBody

    /**
     * 获取收银商品列表
     */
    @POST("amountCommodity/get")
    @FormUrlEncoded
    suspend fun getCashierProductList(
        @Header("WSCX") token: String,
        @Field("name") name: String,
        @Field("page") page: Int,
        @Field("size") size: Int,
        @Field("type") type: String,
        @Field("state")state:Int? // 1：所有上架商品，0：所有下架商品，不传所有商品
    ): ApiResponse<GetCashierProductMsg>

    /**
     * 下架
     */
    @PUT("product/undercarriageProduct/{productId}")
    suspend fun undercarriageProduct(
        @Body requestWithToken: RequestWithToken,
        @Path("productId") productId: String
    ): ResponseBody

    /**
     * 上架
     */
    @PUT("product/putawayProduct/{productId}")
    suspend fun putawayProduct(
        @Body requestWithToken: RequestWithToken,
        @Path("productId") productId: String
    ): ResponseBody

    /**
     * 批量删除收银商品
     */
    @DELETE("biz/product/{productIds}")
    suspend fun delCashierProduct(
        @Path("productIds") productIds: String,
        @Query("appKey") appKey: String,
        @Query("appSecret") appSecret: String
    ): ResponseBody

    /**
     *
     * 获取二级分类下的商品列表
     */
    @GET("biz/getProduct")
    suspend fun getProduct(
        @Header("WSCX") token: String,
        @Query("shopId") shopId: String,
        @Query("customCategoryId") customCategoryId: String,//二级分类Id
        @Query("page") page: String, //请求页码
        @Query("size") size: String, //分页大小
        @Query("sort") sort: String,  //默认：product.sequence,desc   product.totalSales,desc 总销量；product.price,desc 价格； product.addTime,desc 添加时间
        @Query("productState") productState: Int = 1 //管家端传1  收银端传2
    ): ApiResponse<List<CustomCategoryProduct>>
}