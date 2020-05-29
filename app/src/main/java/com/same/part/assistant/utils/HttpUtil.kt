package com.same.part.assistant.utils

import android.os.Handler
import android.os.Looper
import okhttp3.*
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
                    val result = response.body?.string().orEmpty()
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(result)
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }

    /**
     * get请求添加header
     */
    internal fun getUrlWithHeader(
        headName: String,
        headValue: String,
        url: String,
        onSuccess: ((String) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            val request: Request = Request.Builder().url(url).addHeader(headName, headValue).build()
            SingletonHolder.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    SingletonHolder.handler.post {
                        onError?.invoke(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string().orEmpty()
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(result)
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }

    /**
     * post请求
     */
    internal fun postUrl(
        url: String,
        json: String,
        onSuccess: ((String) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            val body = json.toRequestBody()
            val request: Request = Request.Builder().url(url).post(body).build()
            SingletonHolder.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    SingletonHolder.handler.post {
                        onError?.invoke(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string().orEmpty()
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(result)
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }


    /**
     * post请求添加header
     */
    internal fun postUrlWithHeader(
        headName: String,
        headValue: String,
        url: String,
        json: String,
        onSuccess: ((String) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            if (headName.isEmpty() || headValue.isEmpty()) {
                throw RuntimeException("Can't put Header with empty key or value")
            }
            val body = json.toRequestBody()
            val request: Request =
                Request.Builder().url(url).addHeader(headName, headValue).post(body).build()
            SingletonHolder.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    SingletonHolder.handler.post {
                        onError?.invoke(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string().orEmpty()
                    SingletonHolder.handler.post {
                        onSuccess?.invoke(result)
                    }
                }
            })
        } catch (e: Exception) {
            onError?.invoke(e.toString())
        }
    }
}