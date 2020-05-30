package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.qiniu.android.http.ResponseInfo
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.data.model.QiniuMode
import com.same.part.assistant.data.repository.request.HttpRequestManger
import com.same.part.assistant.utils.QiniuManager
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody

class RequestUploadDataViewModel(application: Application) : BaseViewModel(application) {

    var uploadResult = MutableLiveData<QiniuMode>()

    fun uploadData(
        isNeedUploadQiniu: Boolean,
        imagePath: String
    ) {
        //不上传图片
        if (!isNeedUploadQiniu) {
            uploadResult.postValue(null)
            return
        }
        //上传图片至七牛云
        requestResponseBody({ HttpRequestManger.instance.getQiniuToken(CacheUtil.getToken()) }, {
            val response: String = it.string()
            val jsonObject = JSON.parseObject(response)
            val contentJsonObject = jsonObject.getJSONObject("content")
            val token = contentJsonObject.getString("upToken")
            //上传图片名称
            val key = GlobalUtil.generateKey(imagePath, "portrait")
            //上传
            QiniuManager.upload(imagePath, key, token, object : QiniuManager.UploadListener {
                override fun onSuccess(key: String) {
                    uploadResult.postValue(QiniuMode(QiniuManager.getImgUrl(key), null))
                }

                override fun onFailure(info: ResponseInfo?) {
                    uploadResult.postValue(QiniuMode("", info))
                }

                override fun onProgress(percent: Double) {
                }
            })
        })
    }


}