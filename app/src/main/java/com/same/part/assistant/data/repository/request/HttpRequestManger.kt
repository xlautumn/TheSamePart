package com.same.part.assistant.data.repository.request

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.app.network.NetworkApi
import com.same.part.assistant.app.network.NetworkApiv2
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.DEFAULT_SHOP_ID
import com.same.part.assistant.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.network.AppException
import okhttp3.ResponseBody
import retrofit2.http.*

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

            val shopUserModelResponse = getUserInfo(loginData.data.accessToken)
            if (shopUserModelResponse.isSucces()) {
                val token = shopUserModelResponse.data.AccessToken?.accessToken ?: ""
                val userId = shopUserModelResponse.data.UserShopDTO?.takeIf { it.isNotEmpty() }
                    ?.get(0)?.user?.userId?.toString() ?: ""
                val addressMsgResponse = getAddressesByUserId(token, userId)
                if (addressMsgResponse.isSucces()) {
                    shopUserModelResponse.data.AddressMsg = addressMsgResponse.data
                } else {
                    //抛出错误异常
                    throw AppException(loginData.code, loginData.errorMsg)
                }
            } else {
                //抛出错误异常
                throw AppException(loginData.code, loginData.errorMsg)
            }
            return shopUserModelResponse
        } else {
            //抛出错误异常
            throw AppException(loginData.code, loginData.errorMsg)
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(token: String): ApiResponse<ShopUserModel> {
        return NetworkApi.service.getUserInfo(RequestShopUserInfo(token))
    }

    /**
     * 获取用户地址
     */
    suspend fun getAddressesByUserId(
        token: String,
        userId: String
    ): ApiResponse<AddressMsg> {
        return NetworkApi.service.getAddressesByUserid(token, userId)
    }

    /**
     * 获取店铺信息
     */
    suspend fun getShopInfo(token: String): ApiResponse<ShopModel> {
        return NetworkApi.service.getShopInfo(token)
    }

    /**
     * 更新店铺信息
     */
    suspend fun updateShopInfo(
        token: String,
        img: String,
        name: String,
        content: String,
        shopId: String
    ): ResponseBody {
        return NetworkApi.service.updateShopIno(
            token,
            shopId,
            RequestUpdateShopInfo(content, img, name)
        )
    }

    /**
     * 获取七牛Token
     */
    suspend fun getQiniuToken(
        token: String
    ): ResponseBody {
        return NetworkApiv2.service.getQiniuToken(token)
    }

    /**
     * 查询商品分类详情
     */
    suspend fun queryShopCategoryDetail(
        token: String, customCategoryId: String
    ): ResponseBody {
        return NetworkApiv2.service.queryShopCategoryDetail(token, customCategoryId)
    }

    /**
     * 添加商品分类
     */
    suspend fun addShopCategory(
        token: String, requestShopCategory: RequestShopCategoryInfo
    ): ResponseBody {
        return NetworkApiv2.service.addShopCategory(token, requestShopCategory)
    }


    /**
     * 编辑店铺商品分类
     */
    suspend fun editShopCategory(
        customCategoryId: String, requestShopCategory: RequestShopCategoryInfo
    ): ResponseBody {
        return NetworkApiv2.service.editShopCategory(customCategoryId, requestShopCategory)
    }

    /**
     * 添加/更新收银商品
     */
    suspend fun createOrUpdateCashierGood(
        token: String, createOrUpdateGoodsInfo: CreateOrUpdateGoodsInfo
    ): ResponseBody {
        return NetworkApiv2.service.createOrUpdateCashierGood(token, createOrUpdateGoodsInfo)
    }

    /**
     * 获取店铺商品分类列表
     */
    suspend fun getProductClassificationList(
    ): ResponseBody {
        return NetworkApiv2.service.getProductClassificationList(CacheUtil.getToken())
    }

    /**
     * 获取收银商品详情
     */
    suspend fun getCashierGoodDetail(
        productId: String
    ): ApiResponse<CashierDetailMode> {
        return NetworkApi.service.getCashierGoodDetail(CacheUtil.getToken(), productId)
    }

    /**
     * 获取店铺优惠劵活动列表
     */
    suspend fun getCouponsList(
    ): ResponseBody {
        return NetworkApiv2.service.getCouponsList()
    }


    /**
     * 创建优惠劵活动
     */
    suspend fun createCouponActivity(
        requestCreateCouponInfo: RequestCreateCouponInfo
    ): ResponseBody {
        return NetworkApiv2.service.createCouponActivity(requestCreateCouponInfo)
    }

    /**
     * 获取购物车列表
     */
    suspend fun getCartList(
        appKey: String = CacheUtil.getAppKey(),
        appSecret: String = CacheUtil.getAppSecret(),
        userId: Int = CacheUtil.getUserId() ?: 0,
//        shopId: Int = CacheUtil.getShopId() ?: 0
        shopId: Int = DEFAULT_SHOP_ID
    ): ResponseBody {
        return NetworkApi.service.getCartList(appKey, appSecret, userId, shopId)
    }

    /**
     * 创建购物车
     */
    suspend fun createCart(requestCreateCart: RequestCreateCart): ResponseBody {
        return NetworkApi.service.createCart(requestCreateCart)
    }

    /**
     * 修改购物车数量
     */
    suspend fun updateCart(
        cartId: String,
        quantity: Int
    ): ResponseBody {
        return NetworkApi.service.updateCart(cartId, RequestUpdateCart(quantity = quantity))
    }

    /**
     * 批量删除购物车
     */
    suspend fun delCarts(
        appKey: String = CacheUtil.getAppKey(),
        appSecret: String = CacheUtil.getAppSecret(),
        cartIds: String
    ): ResponseBody {
        return NetworkApi.service.delCarts(
            cartIds, appKey, appSecret
        )
    }

    /**
     * 创建商品订单
     */
    suspend fun createOrder(
        addressId: String,
        cartIds: String,
        category: String,
        orderRemarks: List<RequestCreateOrder.OrderRemark>,
        userId: String
    ): ResponseBody {
        return NetworkApi.service.createOrder(
            RequestCreateOrder(
                addressId = addressId,
                cartIds = cartIds,
                category = category,
                orderRemarks = orderRemarks,
                userId = userId
            )
        )
    }

    /**
     * 下单到支付或者微信
     */
    suspend fun getPaySign(
        productOrderId: String,
        payment: String
    ): ResponseBody {
        return NetworkApi.service.getPaySign(productOrderId, RequestPay(payment = payment))
    }


    /**
     * 获取店铺账号列表
     */
    suspend fun getShopAccounts(
    ): ResponseBody {
        return NetworkApiv2.service.getShopAccounts()
    }


    /**
     * 修改账号密码
     */
    suspend fun changeAccountPwd(
        changePwdInfo: ChangePwdInfo
    ): ApiResponse<ChangePwdModel> {
        return NetworkApi.service.changeAccountPwd(changePwdInfo)
    }

    /**
     * 获取店铺会员卡列表
     */
    suspend fun getMemberCardList(
        page: Int
    ): ResponseBody {
        return NetworkApi.service.getMemberCardList(page)
    }

    /**
     * 创建店铺会员卡
     */
    suspend fun createMemberCard(
        createMemberCard: CreateMemberCard
    ): ResponseBody {
        return NetworkApi.service.createMemberCard(createMemberCard)
    }

    /**
     * 编辑店铺会员卡
     */
    suspend fun editMemberCard(
        cardId: String, createMemberCard: CreateMemberCard
    ): ResponseBody {
        return NetworkApi.service.editMemberCard(cardId, createMemberCard)
    }

    /**
     * 获取店铺客户列表
     */
    suspend fun getCustomerList(): ResponseBody {
        return NetworkApi.service.getCustomerList()
    }

    /**
     * 确认收货
     */
    suspend fun conformDelivery(orderId: String): ResponseBody {
        return NetworkApi.service.conformDelivery(orderId)
    }

    /**
     * 删除店铺商品分类
     */
    suspend fun deleteShopCategory(categoryId: String): ResponseBody {
        return NetworkApiv2.service.deleteShopCategory(categoryId)
    }

    /**
     * 请求搜索内容结果
     */

    suspend fun getSearchResult(name: String): ResponseBody{
        return NetworkApiv2.service.getSearchResult(name)
    }
}