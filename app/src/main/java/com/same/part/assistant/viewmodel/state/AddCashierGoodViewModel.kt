package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.same.part.assistant.data.model.EditProductSku
import com.same.part.assistant.data.model.ProductSku
import com.same.part.assistant.data.model.ProductSkuV2
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
    var specState = IntLiveData(0)//多规格 0：没有  1：有
    var editProductSku = MutableLiveData<ArrayList<ProductSkuV2>>()
    var quantity = StringLiveData("")
}