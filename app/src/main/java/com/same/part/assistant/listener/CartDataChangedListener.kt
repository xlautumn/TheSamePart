package com.same.part.assistant.listener

import com.same.part.assistant.model.GoodClassModel

interface CartDataChangedListener<T> {

    fun onChanged()
    fun handleAddToCartModel(isAdd: Boolean, goodModel: GoodClassModel.GoodModel)
    fun handleModifyCartModel(goodModel: GoodClassModel.GoodModel)
}

class OnCartDataChangedListener<T> : CartDataChangedListener<T> {

    private var changed: (() -> Unit)? = null
    private var addToCartData: ((Boolean, GoodClassModel.GoodModel) -> Unit)? = null
    private var modifyCartData: ((GoodClassModel.GoodModel) -> Unit)? = null


    override fun onChanged() {
        changed?.invoke()
    }

    override fun handleAddToCartModel(isAdd: Boolean, goodModel: GoodClassModel.GoodModel) {
        addToCartData?.invoke(isAdd, goodModel)
    }

    override fun handleModifyCartModel(goodModel: GoodClassModel.GoodModel) {
        modifyCartData?.invoke(goodModel)
    }

    fun changed(listener: () -> Unit) {
        changed = listener
    }

    fun addToCartData(listener: (Boolean, GoodClassModel.GoodModel) -> Unit) {
        addToCartData = listener
    }

    fun modifyCartData(listener: (GoodClassModel.GoodModel) -> Unit) {
        modifyCartData = listener
    }

}