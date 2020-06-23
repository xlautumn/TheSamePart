package com.same.part.assistant.data.model

import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.entity.SectionEntity
import java.io.Serializable
import java.lang.reflect.Array
import java.util.*
import kotlin.collections.ArrayList

/**
 * 编辑收银商品规格
 */
data class EditProductSku(
    var barcode: String = "",
    var price: String = "",
    var quantity: String = "",
    var weight: String = "",
    val properties: ArrayList<SkuProperty>
) {
    var isSelect = false
    val propertyString: String
        get() = properties.map { " ${it.project.name}:${it.value.value}" }.joinToString("|")
}

/**
 * 收银商品规格属性
 */
data class SkuProperty(
    val project: ProductSpecSectionEntity.ProductSpec,
    val value: ProductSpecSectionEntity.ProductSpecValue
)


sealed class ProductSpecSectionEntity(val itemType: Int) {

    data class ProductSpec(
        val id: String = UUID.randomUUID().toString(),
        var name: String,
        val specValue: ArrayList<ProductSpecValue>
    ) :
        ProductSpecSectionEntity(SPEC_TITLE) {
        var position = -1
    }

    data class ProductSpecValue(val id: String = UUID.randomUUID().toString(), var value: String) :
        ProductSpecSectionEntity(SPEC_VALUE) {
        var isSelect = false
        var position = -1
    }

    data class ProductSpecButton(var productSpec: ProductSpec) : ProductSpecSectionEntity(SPEC_FOOT)

    companion object {
        const val SPEC_TITLE = 1
        const val SPEC_VALUE = 2
        const val SPEC_FOOT = 3
    }
}

data class ProductSkuV2(
    var barcode: String = "",
    var price: String = "",
    var quantity: String = "",
    var weight: String = "",
    val properties: String //[{"project":"1","value":"2"}]
) : Serializable

data class SkuPropertyV2(
    val project: String,
    val value: String
) : Serializable
