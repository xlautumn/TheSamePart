package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.IntObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

class AddVipCardViewModel (application: Application) : BaseViewModel(application) {

    /** 名称 */
    var name = StringObservableField()

    /** 领取方式 */
    var receiveWay = IntObservableField(1)

    /** 折扣 */
    var discount = StringObservableField()

    /** 描述 */
    var description = StringObservableField()

}