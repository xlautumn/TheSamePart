package com.same.part.assistant.data

import me.hgj.jetpackmvvm.network.BaseResponse
import me.hgj.jetpackmvvm.network.NetworkUtil


/**
 * 描述　:服务器返回数据的基类
 */
class ApiResponse<T>(var code: String,var uuid:String,var errorMsg:String,var data:T) : BaseResponse<T>() {

    override fun isSucces(): Boolean {
        return code == NetworkUtil.RESULT_9000 || code == NetworkUtil.RESULT_8000
    }

    override fun getResponseCode(): String {
        //返回你的code
        return code
    }

    override fun getResponseData(): T {
        //返回你的 data
        return data
    }

    override fun getResponseMsg(): String {
        //返回你的 错误消息
        return errorMsg
    }


}