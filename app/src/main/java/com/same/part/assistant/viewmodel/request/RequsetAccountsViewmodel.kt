package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.same.part.assistant.data.model.AccountModel
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequsetAccountsViewmodel (application: Application) : BaseViewModel(application){

    /**
     * 店铺账号列表
     */
    var accountsResult: MutableLiveData<ArrayList<AccountModel>> =
        MutableLiveData()

    /**
     * 获取店铺账号列表
     */
    fun queryAccounts() {
        requestResponseBody({
            HttpRequestManger.instance.getShopAccounts()
        },{ responsebody ->
            try {
                val response: String = responsebody.string()
                val jsonObject = JSON.parseObject(response)
                val accountList = ArrayList<AccountModel>()
                jsonObject.getJSONArray("msg").takeIf { it.size >= 0 }?.apply {
                    for (i in 0 until size) {
                        getJSONObject(i)?.apply {
                            val userId = getString("userId")?:""
                            val nickname = getString("nickname")?:""
                            val mobile = getString("mobile")?:""
                            val type = getString("type")?:""
                            accountList.add(AccountModel(userId,nickname,mobile,type))
                        }
                    }
                }
                accountsResult.postValue(accountList)
            } catch (e: Exception){
                e.stackTrace
            }
        },isShowDialog = true)
    }

}