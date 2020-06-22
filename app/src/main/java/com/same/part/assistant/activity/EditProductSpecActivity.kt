package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.GsonUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.data.model.ProductSkuV2
import com.same.part.assistant.data.model.SkuPropertyV2
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import me.hgj.jetpackmvvm.base.activity.BaseVmActivity
import java.util.ArrayList

class EditProductSpecActivity : BaseVmActivity<EditProductSpecViewModel>() {
    override fun layoutId(): Int = R.layout.activity_edit_product_spec

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun showLoading(message: String) {

    }

    override fun dismissLoading() {

    }

    override fun createObserver() {

    }

    override fun onBackPressed() {
       val controller =  findNavController(R.id.navHostFragment)
        if (controller.currentDestination?.id ==R.id.editProductSpecDetailFragment){
            if (mViewModel.checkSpecDetail()){
                controller.navigateUp()
            }else{
                showMessage(
                    message = "是否放弃编辑规格明细内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                        mViewModel.clearProductSkuDetail()
                        controller.navigateUp()
                    }
                )
            }
        }else if (controller.currentDestination?.id ==R.id.editProductSpecDetailFragment){
            if (mViewModel.checkSpec()) {
                mViewModel.productSkus.value?.map {
                    val propertiesList = it.properties.map { SkuPropertyV2(it.project.name,it.value.value) }
                    val properties = GsonUtils.toJson(propertiesList)
                    ProductSkuV2(it.barcode,it.price,it.quantity,it.weight,properties)
                }?.apply {
                    val intent = Intent()
                    intent.putExtra(AddCashierGoodActivity.KEY_SETTING_PRODUCT_SPEC,ArrayList(this))
                    setResult(Activity.RESULT_OK,intent)
                }

            } else {
                showMessage(
                    message = "是否放弃设置商品规格内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                       finish()
                    }
                )
            }
        }else{
            super.onBackPressed()
        }


    }


}