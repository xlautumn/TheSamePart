package com.same.part.assistant.data.repository.request

import com.same.part.assistant.data.ApiResponse
import com.same.part.assistant.app.network.NetworkApi
import com.same.part.assistant.app.network.NetworkApiv2
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.*
import me.hgj.jetpackmvvm.network.AppException
import okhttp3.ResponseBody

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
    suspend fun getUserInfo(token: String): ApiResponse<ShopUserModel> {
        return NetworkApi.service.getUserInfo(RequestShopUserInfo(token))
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
        brand: String,
        shopId: String
    ): ResponseBody {
        return NetworkApi.service.updateShopIno(
            token,
            shopId,
            RequestUpdateShopInfo(brand, img, name)
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


}