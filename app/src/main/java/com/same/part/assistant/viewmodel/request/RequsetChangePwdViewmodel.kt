package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.model.AccountModel
import com.same.part.assistant.data.model.ChangePwdInfo
import com.same.part.assistant.data.model.ChangePwdModel
import com.same.part.assistant.data.model.ShopUserModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.state.ResultState

class RequsetChangePwdViewmodel (application: Application) : BaseViewModel(application){

    var changePwdResult = MutableLiveData<ResultState<ChangePwdModel>>()

    /**
     * 修改账号密码
     */
    fun changeAccountPwd(changePwdInfo: ChangePwdInfo) {
        request(
            {  HttpRequestManger.instance.changeAccountPwd(changePwdInfo) }
            , changePwdResult,
            true
        )
    }

}