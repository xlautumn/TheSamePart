package com.same.part.assistant.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.same.part.assistant.app.ext.afterTextChanged
import com.same.part.assistant.data.model.ProductSpecSectionEntity
import com.same.part.assistant.databinding.LayoutProductSpecButtonBinding
import com.same.part.assistant.databinding.LayoutProductSpecTitleBinding
import com.same.part.assistant.databinding.LayoutProductSpecValueBinding
import com.same.part.assistant.fragment.AddProductSpecFragment
import me.hgj.jetpackmvvm.ext.view.afterTextChange

class AddProductSpecAdapter(private val proxyClick: AddProductSpecFragment.ProxyClick) :
    RecyclerView.Adapter<ProductSpecViewHolder>() {

    private val data: MutableList<ProductSpecSectionEntity> = arrayListOf()


    override fun getItemViewType(position: Int): Int {
        return this.data[position].itemType
    }

    override fun onBindViewHolder(holder: ProductSpecViewHolder, position: Int) {

        when (holder) {
            is ProductSpecViewHolder.ProductSpecTitleViewHolder -> (this.data[position] as? ProductSpecSectionEntity.ProductSpec)?.let {
                holder.bind(it)
            }
            is ProductSpecViewHolder.ProductSpecValueViewHolder -> (this.data[position] as? ProductSpecSectionEntity.ProductSpecValue)?.let {
                holder.bind(it)
            }
            is ProductSpecViewHolder.ProductSpecButtonViewHolder -> (this.data[position] as? ProductSpecSectionEntity.ProductSpecButton)?.let {
                holder.bind(it, proxyClick)
            }
        }
    }

    override fun onViewRecycled(holder: ProductSpecViewHolder) {
        super.onViewRecycled(holder)
        when (holder) {
            is ProductSpecViewHolder.ProductSpecTitleViewHolder -> {
                holder.onRecycle()
            }
            is ProductSpecViewHolder.ProductSpecValueViewHolder -> {
                holder.onRecycle()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSpecViewHolder {
        return when (viewType) {
            ProductSpecSectionEntity.SPEC_TITLE -> ProductSpecViewHolder.ProductSpecTitleViewHolder(
                LayoutProductSpecTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ProductSpecSectionEntity.SPEC_VALUE -> ProductSpecViewHolder.ProductSpecValueViewHolder(
                LayoutProductSpecValueBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            ProductSpecSectionEntity.SPEC_FOOT -> ProductSpecViewHolder.ProductSpecButtonViewHolder(
                LayoutProductSpecButtonBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> ProductSpecViewHolder.ProductSpecTitleViewHolder(
                LayoutProductSpecTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int = this.data.size

    fun setData(data: List<ProductSpecSectionEntity>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

}

sealed class ProductSpecViewHolder(binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    class ProductSpecTitleViewHolder(private val binding: LayoutProductSpecTitleBinding) :
        ProductSpecViewHolder(binding) {
        private var textWatcher: TextWatcher? = null
        fun bind(productSpec: ProductSpecSectionEntity.ProductSpec) {
            binding.tvSpecName.setText(productSpec.name)
            textWatcher = binding.tvSpecName.afterTextChanged(afterTextChanged = { productSpec.name = it.trim()})
            binding.tvSpecTitle.text = "规格名${productSpec.position+1}"
        }

        fun onRecycle() {
            textWatcher?.let {
                binding.tvSpecName.removeTextChangedListener(it)
            }
        }
    }

    class ProductSpecValueViewHolder(private val binding: LayoutProductSpecValueBinding) :
        ProductSpecViewHolder(binding) {

        private var textWatcher: TextWatcher? = null

        fun bind(productSpecValue: ProductSpecSectionEntity.ProductSpecValue) {
            binding.tvSpecValueTitle.text = "规格值${productSpecValue.position+1}"
            binding.tvSpecValue.setText(productSpecValue.value)
            textWatcher= binding.tvSpecValue.afterTextChanged(afterTextChanged = { productSpecValue.value = it.trim()})
            binding.ivSpecCheck.isSelected = productSpecValue.isSelect
            binding.ivSpecCheck.setOnClickListener {
                productSpecValue.isSelect = !productSpecValue.isSelect
                binding.ivSpecCheck.isSelected = productSpecValue.isSelect
            }
        }

        fun onRecycle() {
            textWatcher?.let {
                binding.tvSpecValue.removeTextChangedListener(it)
            }
        }
    }

    class ProductSpecButtonViewHolder(private val binding: LayoutProductSpecButtonBinding) :
        ProductSpecViewHolder(binding) {

        fun bind(
            productSpecButton: ProductSpecSectionEntity.ProductSpecButton,
            proxyClick: AddProductSpecFragment.ProxyClick
        ) {
            binding.addButton.setOnClickListener {
                proxyClick.addSpecValue(productSpecButton.productSpec)
            }
            binding.delButton.setOnClickListener {
                proxyClick.delSpecValue(productSpecButton.productSpec)
            }
        }
    }

}