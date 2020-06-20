package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.callback.livedata.IntLiveData
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

class AddCashierGoodViewModel(application: Application) : BaseViewModel(application) {
    var imgs = StringLiveData("")
    var name = StringLiveData("")
    var unit = StringLiveData("个")
    var price = StringLiveData("")
    var sequence = StringLiveData("")
    var barcode = StringLiveData("")
    var productCategoryId = StringLiveData("")
    var type = StringLiveData("否")
    var hasSelectPhoto = BooleanLiveData(false)
    var shelvesState = IntLiveData(1)
    var specState = IntLiveData(0)

    var quantity = StringLiveData("")
}