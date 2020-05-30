package com.same.part.assistant.app.util

import android.text.TextUtils
import com.google.gson.Gson
import com.same.part.assistant.data.model.ShopUserModel
import com.same.part.assistant.data.model.ShopUserLoginModel
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
    fun setShopUserModel(shopUserModel: ShopUserModel?) {
        val kv = MMKV.mmkvWithID("app")
        if (shopUserModel == null) {
            kv.encode("shopUserModel", "")
            setIsLogin(false)
        } else {
            kv.encode("shopUserModel", Gson().toJson(shopUserModel))
            setTokenExpirationTime(shopUserModel.AccessToken.expiresIn + System.currentTimeMillis())
            setIsLogin(true)
        }
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


}