package com.same.part.assistant.app.util

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.same.part.assistant.data.model.Address
import com.same.part.assistant.data.model.ShopUserModel
import com.same.part.assistant.data.model.ShopUserLoginModel
import com.same.part.assistant.utils.SharedPreferenceUtil
import com.tencent.mmkv.MMKV

/**
 * 本地缓存类
 */
object CacheUtil {
    /**
     * 获取保存的账户信息
     */
    fun getShopUserModel(): ShopUserModel? {
        val kv = MMKV.mmkvWithID("app")
        val shopUserModel = kv.decodeString("shopUserModel")
        return if (TextUtils.isEmpty(shopUserModel)) {
            null
        } else {
            Gson().fromJson(shopUserModel, ShopUserModel::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setShopUserModel(context: Context, shopUserModel: ShopUserModel?) {
        val kv = MMKV.mmkvWithID("app")
        if (shopUserModel == null) {
            kv.encode("shopUserModel", "")
            setIsLogin(false)
            setTokenExpirationTime(0)
            setShopImg("")
            setShopName("")
            clearSearchHistory(context)
        } else {
            kv.encode("shopUserModel", Gson().toJson(shopUserModel))
            setTokenExpirationTime(shopUserModel.AccessToken.expiresIn + System.currentTimeMillis())
            setShopImg(shopUserModel.UserShopDTO.takeIf { it.isNotEmpty() }?.get(0)?.shop?.img)
            setShopName(shopUserModel.UserShopDTO.takeIf { it.isNotEmpty() }?.get(0)?.shop?.name)
            setIsLogin(true)
        }
    }

    private fun clearSearchHistory(context: Context) {
        val sp=SharedPreferenceUtil.getInstance(context)
        sp.removeByKey(SharedPreferenceUtil.SEARCH_HISTORY)
    }

    /**
     * 是否已经登录
     */
    fun isLogin(): Boolean {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeBool("login", false)
    }

    /**
     * 设置是否已经登录
     */
    fun setIsLogin(isLogin: Boolean) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("login", isLogin)
    }

    /**
     * 设置TOKEN过期时间
     */
    fun setTokenExpirationTime(expirationTime: Long) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("expirationTime", expirationTime)
    }

    /**
     * 获取TOKEN过期时间
     */
    fun getTokenExpirationTime(): Long {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeLong("expirationTime")
    }

    /**
     * 获取TOKEN
     */
    fun getToken(): String {
        return getShopUserModel()?.AccessToken?.accessToken ?: ""
    }

    /**
     * 获取appKey
     */
    fun getAppKey(): String {
        return getShopUserModel()?.AccessToken?.easyapi?.appKey ?: ""
    }

    /**
     * 获取appSecret
     */
    fun getAppSecret(): String {
        return getShopUserModel()?.AccessToken?.easyapi?.appSecret ?: ""
    }

    /**
     * 获取shopId
     */
    fun getShopId(): Int? {
        return getShopUserModel()?.UserShopDTO?.takeIf { it.isNotEmpty() }
            ?.get(0)?.shop?.shopId
    }

    /**
     * 获取userId
     */
    fun getUserId(): Int? {
        return getShopUserModel()?.UserShopDTO?.takeIf { it.isNotEmpty() }
            ?.get(0)?.user?.userId
    }

    /**
     * 获取头像
     */
    fun getShopImg(): String {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeString("shopImg") ?: ""
    }

    /**
     * 设置本地头像
     */
    fun setShopImg(shopImg: String?) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("shopImg", shopImg)
    }


    /**
     * 获取手机号
     */
    fun getShopPhone(): String? {
        return getShopUserModel()?.UserShopDTO?.takeIf { it.isNotEmpty() }
            ?.get(0)?.shop?.phone
    }

    /**
     * 获取名称
     */
    fun getShopName(): String {
        val kv = MMKV.mmkvWithID("app")
        return kv.decodeString("shopName") ?: ""
    }

    /**
     * 更新名称
     */
    fun setShopName(shopName: String?) {
        val kv = MMKV.mmkvWithID("app")
        kv.encode("shopName", shopName)
    }


    /**
     * 获取地址Id
     */
    fun getAddressId(): String {
        return getAddress()?.addressId ?: ""
    }

    /**
     * 获取详细地址
     */
    fun getDetailAddress(): String {
        return getAddress()?.let {
            "${it.province}${it.city}${it.district}${it.addr}"
        } ?: ""
    }

    /**
     * 获取地址对象
     */
    fun getAddress(): Address? {
        return getShopUserModel()?.AddressMsg?.data.takeUnless { it.isNullOrEmpty() }?.get(0)
    }

}