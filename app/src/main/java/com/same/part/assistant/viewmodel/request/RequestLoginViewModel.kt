package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.ShopUserModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.state.ResultState


/**
 * 登录注册的请求ViewModel
 */
class RequestLoginViewModel(application: Application) : BaseViewModel(application) {

    var loginResult = MutableLiveData<ResultState<ShopUserModel>>()

    fun loginReq(username: String, password: String) {
        request(
            { HttpRequestManger.instance.login(username, password) }
            , loginResult,
            true,
            "正在登录中..."
        )
    }

}