package com.same.part.assistant.viewmodel.request

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.data.model.*
import com.same.part.assistant.data.repository.request.HttpRequestManger
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestResponseBody
import me.hgj.jetpackmvvm.ext.util.showLog
import me.hgj.jetpackmvvm.state.ResultResponseBodyState
import me.hgj.jetpackmvvm.util.LogUtils
import okhttp3.ResponseBody
import kotlin.math.log

/**
 * 会员卡
 */
class RequestVipCardViewModel(application: Application) : BaseViewModel(application) {

    /**页码*/
    private var pageNo = 0

    /**会员卡数据列表*/
    var cardsListResult: MutableLiveData<MemberCardState> = MutableLiveData()

    /**创建/添加店铺会员卡*/
    var memberCardResult = MutableLiveData<Boolean>()

    /**
     * 获取店铺会员卡列表
     */
    fun getMemberCardList(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 0
        }
        requestResponseBody(
            { HttpRequestManger.instance.getMemberCardList(pageNo) },
            { responsebody ->
                try {
                    val response: String = responsebody.string()
                    val jsonObject = JSON.parseObject(response)
                    val cardList = ArrayList<MemberCardModel>()
                    jsonObject.getJSONArray("content")?.takeIf { it.size >= 0 }?.apply {
                        for (i in 0 until size) {
                            getJSONObject(i)?.apply {
                                val lifeTimeJson = getJSONObject("lifetime")
                                val type = lifeTimeJson.getIntValue("type")
                                val day = lifeTimeJson.getString("day")

                                cardList.add(
                                    MemberCardModel(
                                        getIntValue("cardId"),
                                        getString("name"),
                                        getIntValue("discount"),
                                        getIntValue("userCount"),
                                        getString("description") ?: "",
                                        MemberCardModel.Lifetime(type,day)
                                    )
                                )
                            }
                        }
                    }
                    ++pageNo
                    val totalPages = jsonObject.getIntValue("totalPages")
                    cardsListResult.postValue(
                        MemberCardState(
                            pageNo < totalPages,
                            isRefresh,
                            cardList
                        )
                    )
                } catch (e: Exception) {
                    LogUtils.debugInfo(e.toString())
                }
            }, isShowDialog = true
        )
    }


    /**
     * 创建店铺会员卡
     */
    fun createMemberCard(createMemberCard: CreateMemberCard) {
        requestResponseBody(
            { HttpRequestManger.instance.createMemberCard(createMemberCard) }
            , success = { responsebody ->
                try {
                    postMemberCardResult(responsebody)
                } catch (e: Exception) {
                    ToastUtils.showLong(e.toString())
                }
            }, error = {
                ToastUtils.showLong(it.errorMsg)
            }, isShowDialog = true
        )
    }


    /**
     * 编辑店铺会员卡
     */
    fun editMemberCard(cardId: String, createMemberCard: CreateMemberCard) {
        requestResponseBody(
            { HttpRequestManger.instance.editMemberCard(cardId, createMemberCard) }
            , success = { responsebody ->
                postMemberCardResult(responsebody)
            }, error = {
                ToastUtils.showLong(it.errorMsg)
            }, isShowDialog = true
        )
    }

    /**
     * 删除会员卡
     */
    fun delVIPCard(cardId: Int,onSuccess:()->Unit) {
        requestResponseBody(
            { HttpRequestManger.instance.delVIPCard(cardId) },
            success = {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                val msg = jsonObject.getString("message")
                if (jsonObject.getInteger("code") == 1) {
                    onSuccess()
                }
                ToastUtils.showShort(msg)
            },
            error = {
                ToastUtils.showShort(it.errorMsg)
            },isShowDialog = true)
    }

    /**
     * 判断添加/编辑是否成功
     */
    private fun postMemberCardResult(responsebody: ResponseBody) {
        val response: String = responsebody.string()
        val jsonObject = JSON.parseObject(response)
        ToastUtils.showLong(jsonObject.getString("msg"))
        if (jsonObject.getIntValue("code") == 1) {
            memberCardResult.postValue(true)
        }
    }


}