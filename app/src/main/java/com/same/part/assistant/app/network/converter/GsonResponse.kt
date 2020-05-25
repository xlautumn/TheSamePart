package com.same.part.assistant.app.network.converter

data class GsonResponse(var code: String, var uuid:String, var msg:Any) {

    fun isSucces(): Boolean {
        return code == "9000"
    }
}

