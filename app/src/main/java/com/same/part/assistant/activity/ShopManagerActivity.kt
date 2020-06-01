package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.setCanInput
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.app.util.PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA
import com.same.part.assistant.app.util.PhotoPickerUtil.RESULT_CODE_PHOTO_PICKER
import com.same.part.assistant.app.util.PhotoPickerUtil.choosePhoto
import com.same.part.assistant.app.util.PhotoPickerUtil.showPhotoPicker
import com.same.part.assistant.databinding.ActivityShopManagerBinding
import com.same.part.assistant.viewmodel.request.RequestShopManagerViewModel
import com.same.part.assistant.viewmodel.request.RequestUploadDataViewModel
import com.same.part.assistant.viewmodel.state.ShopManagerViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_shop_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import pub.devrel.easypermissions.EasyPermissions

/**
 * 店铺管理
 */
class ShopManagerActivity : BaseActivity<ShopManagerViewModel, ActivityShopManagerBinding>(),
    EasyPermissions.PermissionCallbacks {

    private val mRequestShopManagerViewModel: RequestShopManagerViewModel by lazy { getViewModel<RequestShopManagerViewModel>() }

    private val mRequestUploadDataViewModel: RequestUploadDataViewModel by lazy { getViewModel<RequestUploadDataViewModel>() }

    override fun layoutId(): Int = R.layout.activity_shop_manager

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        //标题
        mToolbarTitle.text = "店铺管理"
        //返回按钮
        mTitleBack.setOnClickListener { finish() }
        //获取店铺信息
        mRequestShopManagerViewModel.shopModelReq(CacheUtil.getToken())
    }

    override fun createObserver() {
        //店铺信息
        mRequestShopManagerViewModel.shopResult.observe(this, Observer { resultState ->
            parseState(resultState, {
                mViewModel.imageUrl.postValue(it.img ?: "")
                mViewModel.shopName.postValue(it.name ?: "")
                mViewModel.shopDesc.postValue(it.brand ?: "")
                mViewModel.operatorName.postValue(it.linkman ?: "")
                mViewModel.operatorPhone.postValue(it.mobile ?: "")
                mViewModel.address.postValue(
                    it.province.plus(it.city).plus(it.district).plus(it.address)
                )
            })
        })
        //七牛云
        mRequestUploadDataViewModel.uploadResult.observe(this, Observer { qiuniuModel ->
            when {
                qiuniuModel == null -> {//没有选择图片时
                    saveEditContent(mViewModel.imageUrl.value)
                }
                qiuniuModel.img.isNotEmpty() -> {//选择了图片上传至七牛云
                    saveEditContent(qiuniuModel.img)
                }
                qiuniuModel.qiniuResponseInfo != null -> {//七牛云上传失败
                    dismissLoading()
                    ToastUtils.showLong("图片上传失败")
                }
            }
        })
        //七牛云上传loading
        mRequestUploadDataViewModel.uploadingResult.observe(this, Observer {
            showLoading(it)
        })
        //更新信息
        mRequestShopManagerViewModel.updateResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val jsonObject = JSON.parseObject(it.string())
                val code = jsonObject.getIntValue("code")
                if (code == 1) {
                    tvEdit.text = "编辑"
                    tvSave.visibility = View.GONE
                    editDesc.setCanInput(false)
                    editShopName.setCanInput(false)
                }
                ToastUtils.showLong(jsonObject.getString("msg"))
            })
        })
    }

    /**
     * 更新
     */
    private fun saveEditContent(imgUrl:String) {
        mRequestShopManagerViewModel.saveEditContent(
            imgUrl,
            mViewModel.shopName.value,
           mViewModel.shopDesc.value
        )
    }

    inner class ProxyClick {
        //选择头像
        fun chooseAvatar() {
            choosePhoto(this@ShopManagerActivity)
        }

        //保存
        fun save() {
            mRequestUploadDataViewModel.uploadData(
                mViewModel.hasSelectPhoto.value,
                mViewModel.imageUrl.value
            )
        }

        //编辑
        fun edit() {
            if (tvEdit.text == "编辑") {
                tvEdit.text = "取消"
                tvSave.visibility = View.VISIBLE
                editDesc.setCanInput(true)
                editShopName.setCanInput(true)
            } else {
                tvEdit.text = "编辑"
                tvSave.visibility = View.GONE
                editDesc.setCanInput(false)
                editShopName.setCanInput(false)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
            showPhotoPicker(this)
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
        if (requestCode == RESULT_CODE_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            Matisse.obtainResult(data)?.takeIf { it.isNotEmpty() }?.apply {
                val imageUrl  = GlobalUtil.getRealFilePath(this@ShopManagerActivity, this[0]) ?: ""
                mViewModel.imageUrl.postValue(imageUrl)
                mViewModel.hasSelectPhoto.postValue(true)
                Glide.with(this@ShopManagerActivity)
                    .load(this[0])
                    .into(userAvatar)
            }
        }
    }
}