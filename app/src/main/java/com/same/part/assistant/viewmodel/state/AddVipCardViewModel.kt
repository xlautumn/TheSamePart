package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.IntObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import org.json.JSONObject

class AddVipCardViewModel (application: Application) : BaseViewModel(application) {

    /** 名称 */
    var name = StringObservableField()

    /** 领取方式 */
    var receiveWay = IntObservableField(1)

    /** 折扣 */
    var discount = StringObservableField()

    /** 描述 */
    var description = StringObservableField()

    /** 卡片有效期 */
    var cardPeriodOfValidity = StringObservableField("0")


    fun getCardPeriodOfValidityJson(): String {
        return if (cardPeriodOfValidity.get() == "0") {
            JSONObject().put("type", 1).toString()
        } else {
            JSONObject().put("type", 2)
                .put("day", cardPeriodOfValidity.get())
                .toString()
        }
    }

}