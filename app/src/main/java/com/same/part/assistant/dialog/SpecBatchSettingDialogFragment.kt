package com.same.part.assistant.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.util.NumberInputUtil
import com.same.part.assistant.databinding.LayoutSpecBatchSettingBinding
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import me.hgj.jetpackmvvm.ext.getActivityViewModel
import me.hgj.jetpackmvvm.ext.view.isTrimEmpty
import me.hgj.jetpackmvvm.ext.view.textStringTrim

class SpecBatchSettingDialogFragment :
    DialogFragment() {

    private lateinit var binding: LayoutSpecBatchSettingBinding

    private var confirmAction: ((price: String, quantity: String) -> Unit)? = null
    private lateinit var viewModel: EditProductSpecViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutSpecBatchSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NumberInputUtil.setPriceMode(binding.etPrice, 2)
        binding.tvConfirm.setOnClickListener {
            if (judgeCanSave()) {
                val price = binding.etPrice.textStringTrim()
                val quantity = binding.etQuantity.textStringTrim()
                confirmAction?.invoke(price, quantity)
                dismiss()
            }
        }
        binding.tvCancel.setOnClickListener { dismiss() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getActivityViewModel()
        binding.etQuantity.digitsWithHintText(viewModel.productType.value == 1)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            setBackgroundDrawableResource(android.R.color.transparent)
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return dialog
    }

    fun setConfirmAction(confirmAction: ((price: String, quantity: String) -> Unit)) {
        this.confirmAction = confirmAction
    }

    private fun judgeCanSave(): Boolean {
        if (binding.etPrice.isTrimEmpty()) {
            ToastUtils.showShort("价格不能为空")
            return false
        }

        if (binding.etQuantity.isTrimEmpty()) {
            ToastUtils.showShort("库存不能为空")
            return false
        }
        return true
    }

    /**
     * 库存hint显示以及小数点位数
     */
    private fun EditText.digitsWithHintText(hasDigits: Boolean) {
        if (hasDigits) {
            hint = "称重商品库存为商品总重量"
            NumberInputUtil.setPriceMode(this, 3)
        } else {
            hint = "非称重商品库存为数量"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

}