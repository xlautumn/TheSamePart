package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

class BusinessManagerModel(application: Application) : BaseViewModel(application) {

    //昨日订单金额
    var incomeToday = StringObservableField()

    //一周订单金额
    var incomeWeek = StringObservableField()

    //一周平均订单金额
    var incomeMonth = StringObservableField()

    //总金额
    var incomeYear = StringObservableField()

}