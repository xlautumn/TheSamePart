package com.same.part.assistant.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.adapter.EditProductSpecDetailAdapter
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.app.util.PhotoPickerUtil
import com.same.part.assistant.app.util.ScanBarCodeUtil
import com.same.part.assistant.databinding.FragmentEditProductSpecDetailBinding
import com.same.part.assistant.utils.Util
import com.same.part.assistant.viewmodel.state.EditProductSpecViewModel
import com.yzq.zxinglibrary.common.Constant
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_add_cashier_good.*
import me.hgj.jetpackmvvm.ext.getActivityViewModel
import pub.devrel.easypermissions.EasyPermissions

class EditProductSpecDetailFragment : Fragment() ,EasyPermissions.PermissionCallbacks {

    private lateinit var binding: FragmentEditProductSpecDetailBinding
    private lateinit var viewModel: EditProductSpecViewModel

    private var scanCodeEditText:EditText?= null
    private val adapter: EditProductSpecDetailAdapter by lazy {
        EditProductSpecDetailAdapter(
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
            if (viewModel.checkSpecDetail()){
                findNavController().navigateUp()
            }else{
                (activity as? AppCompatActivity)?.showMessage(
                    message = "是否放弃编辑规格明细内容",
                    positiveButtonText = "放弃",
                    positiveAction = {
                        viewModel.clearProductSkuDetail()
                        findNavController().navigateUp()
                    }
                )
            }
        }
        binding.tvSaveSpecDetail.setOnClickListener {
            if (viewModel.checkSpecDetail()){
                findNavController().navigateUp()
            }
        }

        binding.tvCheckAll.setOnClickListener {
            val selectAll  = !binding.ivCheckAll.isSelected
            binding.ivCheckAll.isSelected = selectAll
            viewModel.productSkus.value?.forEach { it.isSelect = selectAll }
            adapter.notifyDataSetChanged()
        }

        binding.tvBatchSetting.setOnClickListener {

        }

        binding.rvProductSpecDetail.adapter = adapter
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
        viewModel.productSkus.value?.apply {
            adapter.setData(this)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
            activity?.let { PhotoPickerUtil.showPhotoPicker(it) }
        } else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE) {
            ScanBarCodeUtil.startScanCode(this)
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

    inner class ProxyClick {
        fun scanCode(editText: EditText) {
            scanCodeEditText= editText
            ScanBarCodeUtil.startScanCode(this@EditProductSpecDetailFragment)
        }

    }
}
