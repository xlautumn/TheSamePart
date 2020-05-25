package com.same.part.assistant.app.util

import android.text.TextUtils
import com.google.gson.Gson
import com.same.part.assistant.model.ShopUserLoginModel
import com.tencent.mmkv.MMKV

object CacheUtil {
    /**
     * 获取保存的账户信息
     */
    fun getLoginModel(): ShopUserLoginModel? {
        val kv = MMKV.mmkvWithID("app")
        val userStr = kv.decodeString("loginModel")
        return if (TextUtils.isEmpty(userStr)) {
            null
        } else {
            Gson().fromJson(userStr, ShopUserLoginModel::class.java)
        }
    }

    /**
     * 设置账户信息
     */
    fun setLoginModel(userResponse: ShopUserLoginModel?) {
        val kv = MMKV.mmkvWithID("app")
        if (userResponse == null) {
            kv.encode("loginModel", "")
            setIsLogin(false)
        } else {
            kv.encode("loginModel", Gson().toJson(userResponse))
            setTokenExpirationTime(userResponse.expiresIn + System.currentTimeMillis())
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


}