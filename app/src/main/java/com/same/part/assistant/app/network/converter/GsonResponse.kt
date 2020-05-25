package com.same.part.assistant.app.network.converter

import me.hgj.jetpackmvvm.network.NetworkUtil.RESULT_8000
import me.hgj.jetpackmvvm.network.NetworkUtil.RESULT_9000

data class GsonResponse(var code: String, var uuid:String, var msg:Any) {

    fun isSucces(): Boolean {
        return code == RESULT_9000 || code == RESULT_8000
    }
}

