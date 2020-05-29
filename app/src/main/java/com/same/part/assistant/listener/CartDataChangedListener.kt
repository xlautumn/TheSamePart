package com.same.part.assistant.listener

import com.same.part.assistant.data.model.ProductDetailData

interface CartDataChangedListener<T> {

    fun onChanged()
    fun handleAddToCartModel(isAdd: Boolean, goodModel: ProductDetailData)
    fun handleModifyCartModel(goodModel: ProductDetailData)
}

class OnCartDataChangedListener<T> : CartDataChangedListener<T> {

    private var changed: (() -> Unit)? = null
    private var addToCartData: ((Boolean, ProductDetailData) -> Unit)? = null
    private var modifyCartData: ((ProductDetailData) -> Unit)? = null


    override fun onChanged() {
        changed?.invoke()
    }

    override fun handleAddToCartModel(isAdd: Boolean, goodModel: ProductDetailData) {
        addToCartData?.invoke(isAdd, goodModel)
    }

    override fun handleModifyCartModel(goodModel: ProductDetailData) {
        modifyCartData?.invoke(goodModel)
    }

    fun changed(listener: () -> Unit) {
        changed = listener
    }

    fun addToCartData(listener: (Boolean, ProductDetailData) -> Unit) {
        addToCartData = listener
    }

    fun modifyCartData(listener: (ProductDetailData) -> Unit) {
        modifyCartData = listener
    }

}