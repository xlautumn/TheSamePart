package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.same.part.assistant.R
import com.same.part.assistant.adapter.AddProductSpecAdapter
import com.same.part.assistant.data.model.ProductSpecSectionEntity
import com.same.part.assistant.databinding.FragmentAddProductSpecBinding
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import me.hgj.jetpackmvvm.ext.getActivityViewModel

class AddProductSpecFragment : Fragment() {
    private lateinit var binding: FragmentAddProductSpecBinding
    private lateinit var viewModel: EditProductSpecViewModel
    private val adapter: AddProductSpecAdapter by lazy { AddProductSpecAdapter(ProxyClick()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductSpecBinding.inflate(inflater, container, false)
        binding.toolbarTitle.findViewById<TextView>(R.id.mToolbarTitle).text = "商品规格"
        //返回按钮
        binding.toolbarTitle.findViewById<View>(R.id.mTitleBack).setOnClickListener {
            activity?.finish()
        }
        binding.llProductSpecDetail.setOnClickListener {
            if (viewModel.checkSpec()) {
                viewModel.createEditProductSku()
                findNavController().navigate(R.id.action_addProductSpecFragment_to_editProductSpecDetailFragment)
            }
        }
        binding.tvAddSpec.setOnClickListener {
            viewModel.addSpec()
        }
        binding.tvSaveSpec.setOnClickListener {
            if (viewModel.checkSpec()) {
                viewModel.createEditProductSku()

            }

        }
        binding.rvProductSpec.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getActivityViewModel()
        viewModel.productSpecList.value?.let {
            setData(it)
        }
        viewModel.productSpecList.observe(viewLifecycleOwner, Observer {
            setData(it)
        })
    }

    private fun setData(it: ArrayList<ProductSpecSectionEntity.ProductSpec>) {
        val newList = arrayListOf<ProductSpecSectionEntity>()
        it.forEachIndexed { index, productSpec ->
            productSpec.position = index
            newList.add(productSpec)
            productSpec.specValue.forEachIndexed { index, productSpecValue ->
                productSpecValue.position = index
            }
            newList.addAll(productSpec.specValue)
            newList.add(ProductSpecSectionEntity.ProductSpecButton(productSpec))
        }
        adapter.setData(newList)
    }

    inner class ProxyClick {
        fun addSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
            viewModel.addSpecValue(productSpec)
        }

        fun delSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
            viewModel.delSpecValue(productSpec)

        }
    }
}