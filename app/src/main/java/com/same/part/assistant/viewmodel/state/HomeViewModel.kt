package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

class HomeViewModel(application: Application) : BaseViewModel(application) {

    var imageUrl = StringObservableField()

    var name = StringObservableField()

    var phone = StringObservableField()

    //昨日订单数
    var turnoverCountToday = StringObservableField()

    //昨日订单金额
    var incomeToday = StringObservableField()

    //一周订单数
    var turnoverCountWeek = StringObservableField()

    //一周订单金额
    var incomeWeek = StringObservableField()

    //一周平均订单金额
    var incomeWeekAvg = StringObservableField()

}