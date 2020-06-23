package me.hgj.jetpackmvvm.network

import org.json.JSONObject
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

    constructor(error: Error, e: Throwable?) : super(e){
        errCode = error.getKey().toString()
        val message = if (e is HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                try {
                    val jsonObject = JSONObject(errorBody)
                    val message = jsonObject.optString("message", error.getValue())
                    message
                }catch (e:Exception){
                    error.getValue()
                }
            } else {
                error.getValue()
            }
        } else error.getValue()
        errorMsg = message
        errorLog = e?.message
    }
}