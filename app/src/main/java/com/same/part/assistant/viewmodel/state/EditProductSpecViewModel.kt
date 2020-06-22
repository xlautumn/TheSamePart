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

/**
 * 编辑商品规格
 */
class EditProductSpecViewModel(application: Application) : BaseViewModel(application) {

    /**
     * 商品规格属性map
     */
    private val _productSpecList =
        MutableLiveData<ArrayList<ProductSpecSectionEntity.ProductSpec>>()
    val productSpecList: LiveData<ArrayList<ProductSpecSectionEntity.ProductSpec>> =
        _productSpecList

    init {
        _productSpecList.value = arrayListOf()
        addSpec()
    }

    /**
     * 商品规格集合
     */
    private val _productSkus = MutableLiveData<ArrayList<EditProductSku>>()
    val productSkus: LiveData<ArrayList<EditProductSku>> = _productSkus

    /**
     * 商品规格属性增加规格值
     */
    fun addSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
        productSpec.specValue.apply {
            val id = System.currentTimeMillis()
            this.add(ProductSpecSectionEntity.ProductSpecValue(id = id, value = ""))
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
            val id = System.currentTimeMillis()
            add(
                ProductSpecSectionEntity.ProductSpec(
                    id = id,
                    name = "",
                    specValue = arrayListOf(ProductSpecSectionEntity.ProductSpecValue(System.currentTimeMillis(), ""))
                )
            )
        }
        _productSpecList.value = _productSpecList.value
    }

    /**
     * 检查商品规格值
     * 设置规格明细之前调用
     * 注：只检查规格名和规格值
     */
    fun checkSpec(): Boolean {
        var result = true
        _productSpecList.value?.forEachIndexed { index, productSpec ->
            if (productSpec.name.isEmpty()) {
                ToastUtils.showShort("规格名${productSpec.position+1}不能为空")
                result = false
                return result
            } else {
                productSpec.specValue.forEachIndexed { index, productSpecValue ->
                    if (productSpecValue.value.isEmpty()) {
                        ToastUtils.showShort("规格值${productSpecValue.position+1}不能为空")
                        result = false
                        return result
                    }
                }

                productSpec.specValue.apply {
                    val distinctList = distinctBy { it.value }
                    if (this.size != distinctList.size) {
                        ToastUtils.showShort("规格值不能为重复")
                        result = false
                        return result
                    }
                }
            }
        }
        _productSpecList.value?.apply {
            val distinctList = distinctBy { it.name }
            if (this.size != distinctList.size) {
                ToastUtils.showShort("规格名不能为重复")
                result = false
                return result
            }
        }
        return result
    }

    /**
     * 检查商品规格明细
     */
    fun checkSpecDetail(): Boolean {
        var result = true
        _productSkus.value?.forEachIndexed { index, editProductSku ->
            if (editProductSku.price.isEmpty()) {
                ToastUtils.showShort("规格${editProductSku.propertyString}的价格不能为空")
                result = false
                return result
            }
            if (editProductSku.quantity.isEmpty()) {
                ToastUtils.showShort("规格${editProductSku.propertyString}的库存不能为空")
                result = false
                return result
            }
            if (editProductSku.weight.isEmpty()) {
                ToastUtils.showShort("规格${editProductSku.propertyString}的重量不能为空")
                result = false
                return result
            }
            if (editProductSku.barcode.isEmpty()) {
                ToastUtils.showShort("规格${editProductSku.propertyString}的商品条码不能为空")
                result = false
                return result
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
        val oldProductSkus = _productSkus.value.takeUnless { it.isNullOrEmpty() }?.let { ArrayList(it) }?: arrayListOf()
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
            }else{
                val key = skuProperties.joinToString()
                oldProductSkus.find {
                    it.properties.joinToString() == key
                } ?: EditProductSku(properties = skuProperties)
            }
        }?.let {
            editProductSkuResult.addAll(it)
        }
        LogUtils.d("createEditProductSku $editProductSkuResult")
        _productSkus.value = editProductSkuResult
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
