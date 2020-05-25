package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData


/**
 * 描述　:登录注册的Viewmodel
 */
class LoginViewModel(application: Application) : BaseViewModel(application) {
    //手机号
    var phoneNum = StringLiveData()

    var password = StringLiveData()


}