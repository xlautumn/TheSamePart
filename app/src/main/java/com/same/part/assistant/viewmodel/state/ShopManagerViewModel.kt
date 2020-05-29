package com.same.part.assistant.viewmodel.state
import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

class ShopManagerViewModel (application: Application): BaseViewModel(application) {

    var imageUrl = StringLiveData("")

    var shopName = StringLiveData("")

    var shopDesc = StringLiveData("")

    var operatorName = StringLiveData("")

    var operatorPhone = StringLiveData("")

    var operatorMaterial = StringLiveData("")

    var address = StringLiveData("")


    var shopId = StringLiveData("")

}