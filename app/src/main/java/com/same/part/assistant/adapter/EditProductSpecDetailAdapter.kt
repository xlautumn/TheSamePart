package com.same.part.assistant.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.app.ext.afterTextChanged
import com.same.part.assistant.app.util.NumberInputUtil
import com.same.part.assistant.data.model.EditProductSku
import com.same.part.assistant.data.model.ProductSpecSectionEntity
import com.same.part.assistant.databinding.LayoutSpecDetailItemBinding
import com.same.part.assistant.fragment.EditProductSpecDetailFragment
import me.hgj.jetpackmvvm.ext.view.afterTextChange

class EditProductSpecDetailAdapter(
    private val productType: Int,
    private val proxyClick: EditProductSpecDetailFragment.ProxyClick
) : RecyclerView.Adapter<EditProductSpecDetailViewHolder>() {
    private val data: MutableList<EditProductSku> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditProductSpecDetailViewHolder {
        return EditProductSpecDetailViewHolder(
            LayoutSpecDetailItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: EditProductSpecDetailViewHolder, position: Int) {
        holder.bind(productType, data[position], proxyClick)
    }

    override fun onViewRecycled(holder: EditProductSpecDetailViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycle()
    }

    fun setData(data: List<EditProductSku>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}

class EditProductSpecDetailViewHolder(private val binding: LayoutSpecDetailItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        productType: Int,
        productSku: EditProductSku,
        proxyClick: EditProductSpecDetailFragment.ProxyClick
    ) {
        binding.productWeightGroup.visibility = if (productType == 1) View.GONE else View.VISIBLE
        binding.tvSpecProp.text = productSku.propertyString
        binding.ivSpecCheck.isSelected = productSku.isSelect
        binding.ivSpecCheck.setOnClickListener {
            productSku.isSelect = !productSku.isSelect
            binding.ivSpecCheck.isSelected = productSku.isSelect
            proxyClick.productSkuDataStateChanged()
        }

        /**
         * 价格
         */
        NumberInputUtil.setPriceMode(binding.etPrice, 2)
        binding.etPrice.setText(productSku.price)
        val priceTextWatcher = binding.etPrice.afterTextChanged(afterTextChanged = {
            productSku.price = it.trim()
        })
        binding.etPrice.tag = priceTextWatcher

        /**
         * 库存
         */
        binding.etQuantity.digitsWithHintText(productType == 1)
        binding.etQuantity.setText(productSku.quantity)
        val quantityTextWatcher = binding.etQuantity.afterTextChanged(afterTextChanged = {
            productSku.quantity = it.trim()
        })
        binding.etQuantity.tag = quantityTextWatcher

        /**
         * 重量
         */
        NumberInputUtil.setPriceMode(binding.etWeight, 2)
        binding.etWeight.setText(productSku.weight)
        val weightTextWatcher = binding.etWeight.afterTextChanged(afterTextChanged = {
            productSku.weight = it.trim()
        })
        binding.etWeight.tag = weightTextWatcher

        /**
         * 商品条码
         */
        binding.etBarcode.setText(productSku.barcode)
        val barcodeTextWatcher = binding.etBarcode.afterTextChanged(afterTextChanged = {
            productSku.barcode = it.trim()
        })
        binding.etBarcode.tag = barcodeTextWatcher
        binding.scanBarCode.setOnClickListener { proxyClick.scanCode(binding.etBarcode) }
    }

    fun onRecycle() {
        val priceTextWatcher = binding.etPrice.tag as? TextWatcher
        priceTextWatcher?.let {
            binding.etPrice.removeTextChangedListener(it)
        }

        val quantityTextWatcher = binding.etPrice.tag as? TextWatcher
        quantityTextWatcher?.let {
            binding.etQuantity.removeTextChangedListener(it)
        }

        val weightTextWatcher = binding.etPrice.tag as? TextWatcher
        weightTextWatcher?.let {
            binding.etWeight.removeTextChangedListener(it)
        }

        val barcodeTextWatcher = binding.etPrice.tag as? TextWatcher
        barcodeTextWatcher?.let {
            binding.etBarcode.removeTextChangedListener(it)
        }

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