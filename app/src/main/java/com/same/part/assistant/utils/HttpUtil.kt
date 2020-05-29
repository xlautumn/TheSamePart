package com.same.part.assistant.utils

import android.os.Handler
import android.os.Looper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


/**
 * 网络请求
 */
class HttpUtil private constructor() {
    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = HttpUtil()
        val client = OkHttpClient()
        val handler = Handler(Looper.getMainLooper())
    }

    /**
     * get请求
     */
    internal fun getUrl(
        url: String,
        onSuccess: ((String) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            val request: Request = Request.Builder().url(url).build()
            SingletonHolder.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    SingletonHolder.handler.post {
                        onError?.invoke(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(response.body.toString())
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }

    /**
     * get请求
     */
    internal fun postUrl(
        url: String,
        json: String,
        onSuccess: ((String) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            val body = json.toRequestBody("application/json;charset=utf-8".toMediaType())
            val request: Request = Request.Builder().url(url).post(body).build()
            SingletonHolder.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    SingletonHolder.handler.post {
                        onError?.invoke(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(response.body.toString())
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }
}