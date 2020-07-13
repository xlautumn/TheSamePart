package com.same.part.assistant.viewmodel.state

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.app.util.DatetimeUtil
import com.same.part.assistant.data.model.EditProductSku
import com.same.part.assistant.data.model.ProductSpecSectionEntity
import com.same.part.assistant.data.model.SkuProperty
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.BooleanLiveData
import me.hgj.jetpackmvvm.callback.livedata.IntLiveData

/**
 * 编辑商品规格
 */
class EditProductSpecViewModel(application: Application) : BaseViewModel(application) {

    val productType = IntLiveData(0)

    /**
     * 商品规格属性map
     */
    private val _productSpecList =
        MutableLiveData<ArrayList<ProductSpecSectionEntity.ProductSpec>>()
    val productSpecList: LiveData<ArrayList<ProductSpecSectionEntity.ProductSpec>> =
        _productSpecList

    init {
        _productSpecList.value = arrayListOf()
    }

    /**
     * 商品规格明细集合
     */
    private val _productSkus = MutableLiveData<ArrayList<EditProductSku>>()
    val productSkus: LiveData<ArrayList<EditProductSku>> = _productSkus

    /**
     * 规格明细状态
     * false:未设置 true：已设置
     */
    val productSkuState = BooleanLiveData(false)

    /**
     * 商品规格属性增加规格值
     */
    fun addSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
        productSpec.specValue.apply {
            this.add(ProductSpecSectionEntity.ProductSpecValue(value = ""))
        }
        _productSpecList.value = _productSpecList.value
    }

    /**
     * 商品规格属性删除规格值
     */
    fun delSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
        var hasRemove = false
        val delList = arrayListOf<ProductSpecSectionEntity.ProductSpecValue>()
        val iterator = productSpec.specValue.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.isSelect) {
                hasRemove = true
                delList.add(item)
                iterator.remove()
            }
        }
        if (productSpec.specValue.isEmpty()) {
            _productSpecList.value?.remove(productSpec)
            //同步删除含有该规格的规格明细
            _productSkus.value?.takeIf { it.isNotEmpty() }?.apply {
                forEach {
                    val iterator = it.properties.iterator()
                    while (iterator.hasNext()) {
                        val skuProperty = iterator.next()
                        if (productSpec.id == skuProperty.project.id) {
                            iterator.remove()
                        }
                    }
                }
            }
        } else if (hasRemove) {
            //同步删除含有该规格值的规格明细
            _productSkus.value?.takeIf { it.isNotEmpty() }?.apply {
                forEach {
                    val iterator = it.properties.iterator()
                    while (iterator.hasNext()) {
                        val skuProperty = iterator.next()
                        delList.forEach { delSpecValue ->
                            if (productSpec.id == skuProperty.project.id && delSpecValue.id == skuProperty.value.id) {
                                iterator.remove()
                            }
                        }
                    }
                }
            }
        }
        if (hasRemove) {
            _productSpecList.value = _productSpecList.value
        }
    }

    /**
     * 增加商品规格属性
     */
    fun addSpec() {
        _productSpecList.value?.apply {
            add(
                ProductSpecSectionEntity.ProductSpec(
                    name = "",
                    specValue = arrayListOf(
                        ProductSpecSectionEntity.ProductSpecValue(
                            value = ""
                        )
                    )
                )
            )
        }
        clearProductSkuDetail()
        _productSpecList.value = _productSpecList.value
    }

    /**
     * 检查商品规格值
     * 设置规格明细之前调用
     * 注：只检查规格名和规格值
     */
    fun checkSpec(showToast: Boolean = true): Boolean {
        var result = true
        _productSpecList.value?.forEachIndexed { index, productSpec ->
            if (productSpec.name.isEmpty()) {
                if (showToast) {
                    ToastUtils.showShort("规格名${productSpec.position + 1}不能为空")
                }
                result = false
                return result
            } else {
                productSpec.specValue.forEachIndexed { index, productSpecValue ->
                    if (productSpecValue.value.isEmpty()) {
                        if (showToast) {
                            ToastUtils.showShort("规格值${productSpecValue.position + 1}不能为空")
                        }
                        result = false
                        return result
                    }
                }

                productSpec.specValue.apply {
                    val distinctList = distinctBy { it.value }
                    if (this.size != distinctList.size) {
                        if (showToast) {
                            ToastUtils.showShort("规格值不能为重复")
                        }
                        result = false
                        return result
                    }
                }
            }
        }
        _productSpecList.value?.apply {
            val distinctList = distinctBy { it.name }
            if (this.size != distinctList.size) {
                if (showToast) {
                    ToastUtils.showShort("规格名不能为重复")
                }
                result = false
                return result
            }
        }
        return result
    }

    /**
     * 检查商品规格明细
     */
    fun checkSpecDetail(showToast: Boolean = true): Boolean {
        var result = true
        _productSkus.value?.forEachIndexed { index, editProductSku ->
            if (editProductSku.price.isEmpty()) {
                if (showToast) {
                    ToastUtils.showShort("规格${editProductSku.propertyString}的价格不能为空")
                }
                result = false
                return result
            }
            if (editProductSku.quantity.isEmpty()) {
                if (showToast) {
                    ToastUtils.showShort("规格${editProductSku.propertyString}的库存不能为空")
                }
                result = false
                return result
            }

            if (productType.value != 1) {
                if (editProductSku.weight.isEmpty()) {
                    if (showToast) {
                        ToastUtils.showShort("规格${editProductSku.propertyString}的重量不能为空")
                    }
                    result = false
                    return result
                }
//                if (editProductSku.barcode.isEmpty()) {
//                    if (showToast) {
//                        ToastUtils.showShort("规格${editProductSku.propertyString}的商品条码不能为空")
//                    }
//                    result = false
//                    return result
//                }
            }
        }

        if (result && productType.value != 1) {
            result = _productSkus.value?.filter {it.barcode.isNotEmpty()  }?.let {
                it.distinctBy { editProductSku -> editProductSku.barcode }.size.let { size -> size == it.size }
            } ?: true
            if (!result) {
                if (showToast) {
                    ToastUtils.showShort("规格的商品条码不能重复")
                }
            }
        }
        return result
    }

    /**
     * 清除商品规格明细数据
     */
    fun clearProductSkuDetail() {
        _productSkus.value?.clear()
    }

    /**
     *创建_productSkus的值
     */
    fun createEditProductSku() {

        fun getKey(skuProperties: ArrayList<SkuProperty>): String {
            //规格名和规格值的id作为key
            return skuProperties.fold(StringBuffer()) { acc, skuProperty ->
                acc.append("projectId:")
                    .append(skuProperty.project.id)
                    .append("|valueId:")
                    .append(skuProperty.value.id)
            }.toString()
        }

        val oldProductSkus =
            _productSkus.value.takeUnless { it.isNullOrEmpty() }?.let { ArrayList(it) }
                ?: arrayListOf()
        val editProductSkuResult = arrayListOf<EditProductSku>()
        _productSpecList.value?.map { spec ->
            val list = arrayListOf<SkuProperty>()
            spec.specValue.forEach { specValue ->
                list.add(SkuProperty(spec, specValue))
            }
            list
        }?.let {
            var result = listOf<ArrayList<SkuProperty>>()
            it.forEachIndexed { index, arrayList ->
                result = if (index == 0) {
                    arrayList.map { arrayListOf(it) }
                } else {
                    cartesianProduct(result, arrayList)
                }
            }
            result
        }?.map { skuProperties ->
            if (oldProductSkus.isNullOrEmpty()) {
                EditProductSku(properties = skuProperties)
            } else {
                val key = getKey(skuProperties)
                oldProductSkus.find {
                    getKey(it.properties) == key
                } ?: EditProductSku(properties = skuProperties)
            }
        }?.let {
            editProductSkuResult.addAll(it)
        }
        LogUtils.d("createEditProductSku $editProductSkuResult")
        _productSkus.value = editProductSkuResult
    }

    fun setProductSkus(
        productSpecList: List<ProductSpecSectionEntity.ProductSpec>,
        editProductSkuList: List<EditProductSku>
    ) {
        _productSpecList.value = ArrayList(productSpecList)
        _productSkus.value = ArrayList(editProductSkuList)
    }

    /**
     * 笛卡尔乘积
     */
    private fun cartesianProduct(
        result: List<ArrayList<SkuProperty>>,
        list: List<SkuProperty>
    ): List<ArrayList<SkuProperty>> {
        return result.map {
            val tempResult = arrayListOf<ArrayList<SkuProperty>>()
            list.forEach { skuProperty ->
                val newList = ArrayList(it)
                newList.add(skuProperty)
                tempResult.add(newList)
            }
            tempResult
        }.flatMap(fun(it: ArrayList<ArrayList<SkuProperty>>): Iterable<ArrayList<SkuProperty>> {
            return it
        })
    }
}
