package com.same.part.assistant.app.event

import android.app.Application
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.ShopUserModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.UnPeekLiveData
import me.hgj.jetpackmvvm.callback.livedata.UnPeekNotNullLiveData

/**
 * 描述　:APP全局的Viewmodel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppViewModel(app: Application) : BaseViewModel(app) {

    //是否已经登录过
    var isLogin  =
        UnPeekNotNullLiveData<Boolean>()

    //App的账户信息 使用UnPeekLiveData 因为账户信息有可能为空
    var shopUserModel = UnPeekLiveData<ShopUserModel>()

    init {
        //初始化
        isLogin.value = CacheUtil.isLogin()
        //默认值保存的账户信息，没有登陆过则为null
        shopUserModel.value = CacheUtil.getShopUserModel()
    }
}