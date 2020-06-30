package com.same.part.assistant.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.adapter.EditProductSpecDetailAdapter
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.app.util.PhotoPickerUtil
import com.same.part.assistant.app.util.ScanBarCodeUtil
import com.same.part.assistant.databinding.FragmentEditProductSpecDetailBinding
import com.same.part.assistant.dialog.SpecBatchSettingDialogFragment
import com.same.part.assistant.utils.Util
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import com.yzq.zxinglibrary.common.Constant
import me.hgj.jetpackmvvm.ext.getActivityViewModel
import pub.devrel.easypermissions.EasyPermissions

class EditProductSpecDetailFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentEditProductSpecDetailBinding
    private lateinit var viewModel: EditProductSpecViewModel

    private var scanCodeEditText: EditText? = null
    private val adapter: EditProductSpecDetailAdapter by lazy {
        EditProductSpecDetailAdapter(
            viewModel.productType.value,
            ProxyClick()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProductSpecDetailBinding.inflate(inflater, container, false)
        binding.toolbarTitle.findViewById<TextView>(R.id.mToolbarTitle).text = "编辑规格明细"
        //返回按钮
        binding.toolbarTitle.findViewById<View>(R.id.mTitleBack).setOnClickListener {
            if (viewModel.checkSpecDetail(false)) {
                viewModel.productSkuState.value = true
                findNavController().navigateUp()
            } else {
                viewModel.productSkuState.value = false
                (activity as? AppCompatActivity)?.showMessage(
                    message = "是否放弃编辑规格明细内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                        viewModel.clearProductSkuDetail()
                        findNavController().navigateUp()
                    },
                    negativeButtonText = "取消"
                )
            }
        }
        binding.tvSaveSpecDetail.setOnClickListener {
            if (viewModel.checkSpecDetail()) {
                viewModel.productSkuState.value = true
                findNavController().navigateUp()
            }else{
                viewModel.productSkuState.value = false
            }
        }

        binding.tvCheckAll.setOnClickListener {
            val selectAll = !binding.ivCheckAll.isSelected
            binding.ivCheckAll.isSelected = selectAll
            viewModel.productSkus.value?.forEach { it.isSelect = selectAll }
            adapter.notifyDataSetChanged()
        }

        //批量设置规格明细
        binding.tvBatchSetting.setOnClickListener {
            val selectList = viewModel.productSkus.value?.filter { it.isSelect }
            if (selectList.isNullOrEmpty()) {
                ToastUtils.showShort("还未选择需要设置的规格")
            } else {
                SpecBatchSettingDialogFragment().apply {
                    setConfirmAction { price, quantity ->
                        selectList.forEach {
                            it.isSelect = false
                            it.price = price
                            it.quantity = quantity
                        }
                        adapter.notifyDataSetChanged()
                        binding.ivCheckAll.isSelected = false
                    }
                    show(this@EditProductSpecDetailFragment.childFragmentManager, "设置规格")
                }
            }
        }

        binding.rvProductSpecDetail.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.set(0, Util.dip2px(view.context, 6f), 0, 0)
            }
        })
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = getActivityViewModel()
        binding.rvProductSpecDetail.adapter = adapter
        viewModel.productSkus.value?.apply {
            adapter.setData(this)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
        } else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE) {
            ToastUtils.showShort("扫描商品条形码需要访问您的存储权限和照相机权限,请在手机设置中打开以上权限")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
            activity?.let { PhotoPickerUtil.showPhotoPicker(it) }
        } else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE) {
            if (perms.size == ScanBarCodeUtil.PERMISSIONS_REQUEST_LIST.size) {
                ScanBarCodeUtil.startScanCode(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //扫商品条形码
            if (requestCode == ScanBarCodeUtil.REQUEST_CODE_SCAN) {
                val content = data?.getStringExtra(Constant.CODED_CONTENT).orEmpty()
                scanCodeEditText?.setText(content)
                scanCodeEditText = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val selectList = viewModel.productSkus.value?.filter { it.isSelect }
        selectList?.forEach { it.isSelect = false }
    }

    inner class ProxyClick {
        fun scanCode(editText: EditText) {
            scanCodeEditText = editText
            ScanBarCodeUtil.startScanCode(this@EditProductSpecDetailFragment)
        }

        fun productSkuDataStateChanged(){
            viewModel.productSkus.value?.apply {
                val allCheck = this.none { editProductSku ->!editProductSku.isSelect }
                binding.ivCheckAll.isSelected = allCheck
            }
        }

    }
}
