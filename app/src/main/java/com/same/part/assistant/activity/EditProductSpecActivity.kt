package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.reflect.TypeToken
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.data.model.*
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import me.hgj.jetpackmvvm.base.activity.BaseVmActivity
import java.io.Serializable
import java.util.ArrayList

class EditProductSpecActivity : BaseVmActivity<EditProductSpecViewModel>() {
    override fun layoutId(): Int = R.layout.activity_edit_product_spec

    override fun initView(savedInstanceState: Bundle?) {
        val type = intent.getIntExtra(KEY_PRODUCT_TYPE, PRODUCT_TYPE_NO_WEIGHT)
        mViewModel.productType.value = type
        val productSkuV2  = intent.getSerializableExtra(KEY_PRODUCT_SPEC) as? ArrayList<ProductSkuV2>
        productSkuV2?.let {
            convertProductSkuV2(it)
        }

    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

    override fun createObserver() {

    }

    override fun onBackPressed() {
        val controller = findNavController(R.id.navHostFragment)
        if (controller.currentDestination?.id == R.id.editProductSpecDetailFragment) {
            if (mViewModel.checkSpecDetail(false)) {
                mViewModel.productSkuState.value = true
                controller.navigateUp()
            } else {
                mViewModel.productSkuState.value = false
                showMessage(
                    message = "是否放弃编辑规格明细内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                        mViewModel.clearProductSkuDetail()
                        controller.navigateUp()
                    },
                    negativeButtonText = "取消"
                )
            }
        } else if (controller.currentDestination?.id == R.id.addProductSpecFragment) {
            if (mViewModel.productSkuState.value) {
                saveProductSpec()
            } else {
                showMessage(
                    message = "是否放弃设置商品规格内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                        finish()
                    },
                    negativeButtonText = "取消"
                )
            }
        } else {
            super.onBackPressed()
        }


    }

    /**
     * 保存商品规格
     */
    fun saveProductSpec() {
        mViewModel.productSkus.value?.map {
            val propertiesList =
                it.properties.map { SkuPropertyV2(it.project.name, it.value.value) }
            val properties = GsonUtils.toJson(propertiesList)
            ProductSkuV2(it.barcode, it.price, it.quantity, it.weight, properties)
        }?.apply {
            val intent = Intent()
            intent.putExtra(
                AddCashierGoodActivity.KEY_SETTING_PRODUCT_SPEC,
                ArrayList(this)
            )
            setResult(Activity.RESULT_OK, intent)
            finish()
        } ?: kotlin.run{ ToastUtils.showShort("还未设置规格明细") }
    }

    /**
     * 转化商品规格数据
     */
    private fun convertProductSkuV2(productSkuV2List: ArrayList<ProductSkuV2>) {
        data class TempProductSku(
            var barcode: String = "",
            var price: String = "",
            var quantity: String = "",
            var weight: String = "",
            val properties: List<SkuPropertyV2> //[{"project":"1","value":"2"}]
        )

        val tempProductSkuList = productSkuV2List.map {
            val properties = GsonUtils.fromJson<ArrayList<SkuPropertyV2>>(it.properties,
                object :TypeToken<ArrayList<SkuPropertyV2>>(){}.type)
            TempProductSku(it.barcode,it.price,it.quantity,it.weight,properties)
        }

        val skuMap = tempProductSkuList.flatMap { it.properties }.groupBy { it.project }.mapValues {
            val productSpecValueList =
                ArrayList(it.value.map { ProductSpecSectionEntity.ProductSpecValue(value = it.value) })
            ProductSpecSectionEntity.ProductSpec(
                name = it.key,
                specValue = productSpecValueList
            )
        }

        tempProductSkuList.map {
           val properties =  ArrayList(it.properties.map { SkuProperty(skuMap[it.project]?: ProductSpecSectionEntity.ProductSpec(name = "",
                specValue = arrayListOf()), ProductSpecSectionEntity.ProductSpecValue(value = it.value))})
            EditProductSku(it.barcode,it.price,it.quantity,it.weight,properties)
        }.let {
            mViewModel.setProductSkus(skuMap.values.toList(),it)
            mViewModel.productSkuState.value = true
        }
    }

    companion object {
        const val KEY_PRODUCT_TYPE = "KEY_PRODUCT_TYPE"
        const val KEY_PRODUCT_SPEC = "KEY_PRODUCT_SPEC"

        /**
         * 称重商品
         */
        const val PRODUCT_TYPE_WEIGHT = 1

        /**
         * 非称重商品
         */
        const val PRODUCT_TYPE_NO_WEIGHT = 0
    }

}