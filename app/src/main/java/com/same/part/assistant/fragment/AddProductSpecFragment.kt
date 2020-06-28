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
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.EditProductSpecActivity
import com.same.part.assistant.adapter.AddProductSpecAdapter
import com.same.part.assistant.app.ext.showMessage
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
            activity?.onBackPressed()
        }
        binding.llProductSpecDetail.setOnClickListener {
            if (viewModel.checkSpec()) {
                viewModel.createEditProductSku()
                findNavController().navigate(R.id.action_addProductSpecFragment_to_editProductSpecDetailFragment)
            }
        }
        binding.tvAddSpec.setOnClickListener {
            viewModel.productSpecList.value?.let {
                if (it.size >= 2) {
                    ToastUtils.showShort("最多只能添加两个规格！")
                } else {
                    if (viewModel.productSkuState.value) {
                        showMessage(
                            "此时添加规格，将会清除已有的规格明细，是否确定添加",
                            positiveButtonText = "确定",
                            positiveAction = {
                                viewModel.addSpec()
                                viewModel.productSkuState.value = false
                            },
                            negativeButtonText = "取消"
                        )
                    } else {
                        viewModel.addSpec()
                        viewModel.productSkuState.value = false
                    }
                }
            }

        }
        binding.tvSaveSpec.setOnClickListener {
            if (viewModel.productSkuState.value) {
                (activity as? EditProductSpecActivity)?.saveProductSpec()
            } else {
                ToastUtils.showShort("还未设置规格明细")
            }

        }
        binding.rvProductSpec.adapter = adapter
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getActivityViewModel()
//        viewModel.productSpecList.value?.let {
//            setData(it)
//        }
        viewModel.productSpecList.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                viewModel.addSpec()
            }
            setData(it)
        })
//       viewModel.productSkuState.value?.let {
//           binding.tvSpecReminder.text = if (it)"已设置，点击查看" else "未设置，点击设置"
//       }
        viewModel.productSkuState.observe(viewLifecycleOwner, Observer {
            binding.tvSpecReminder.text = if (it) "已设置，点击查看" else "未设置，点击设置"
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
            viewModel.productSkuState.value = false
        }

        fun delSpecValue(productSpec: ProductSpecSectionEntity.ProductSpec) {
            if (viewModel.productSkuState.value&& productSpec.specValue.size==1&&productSpec.specValue[0].isSelect){
                showMessage(
                    "规格至少需要一个规格值，将会把该规格一起删除，以及规格明细有可能清空，是否确定删除",
                    positiveButtonText = "确定",
                    positiveAction = {
                        viewModel.delSpecValue(productSpec)
                        if (!viewModel.productSkuState.value && viewModel.checkSpecDetail(false)) {
                            viewModel.productSkuState.value = true
                        }
                    },
                    negativeButtonText = "取消"
                )
            }else {
                viewModel.delSpecValue(productSpec)
                if (!viewModel.productSkuState.value && viewModel.checkSpecDetail(false)) {
                    viewModel.productSkuState.value = true
                }
            }
        }
    }
}