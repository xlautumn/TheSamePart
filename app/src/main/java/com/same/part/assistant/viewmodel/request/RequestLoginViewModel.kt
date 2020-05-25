package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.model.ShopUserLoginModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState


/**
 * 描述　:登录注册的请求ViewModel
 */
class RequestLoginViewModel(application: Application) : BaseViewModel(application) {
    var loginResult = MutableLiveData<ResultState<ShopUserLoginModel>>()

    fun loginReq(username: String, password: String) {
        //1.这种是在 Activity/Fragment的监听回调中拿到已脱壳的数据（项目有基类的可以用）
        request(
            { HttpRequestManger.instance.login(username, password) }//请求体
            , loginResult,//请求的返回结果，请求成功与否都会改变该值，在Activity或fragment中监听回调结果，具体可看loginActivity中的回调
            true,//是否显示等待框，，默认false不显示 可以默认不传
            "正在登录中..."//等待框内容，可以默认不填请求网络中...
        )
    }

}