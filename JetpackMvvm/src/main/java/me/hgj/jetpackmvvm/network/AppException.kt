package me.hgj.jetpackmvvm.network

import retrofit2.HttpException

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/17
 * 描述　:自定义错误信息异常
 */
class AppException : Exception {

    var errorMsg: String //错误消息
    var errCode: String  //错误码
    var errorLog: String? //错误日志

    constructor(errCode: String?, error: String?, errorLog: String? = "") : super(error) {
        this.errorMsg = error ?: "请求失败，请稍后再试"
        this.errCode = errCode ?: "0"
        this.errorLog = errorLog ?: this.errorMsg
    }

    constructor(error: Error, e: Throwable?) {
        errCode = error.getKey().toString()
        val message = if (e is HttpException) {
            e.response()?.errorBody()?.string()?:error.getValue()
        } else error.getValue()
        errorMsg = message
        errorLog = e?.message
    }
}