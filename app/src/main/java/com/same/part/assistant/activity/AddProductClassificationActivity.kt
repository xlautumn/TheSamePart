package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.app.util.PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA
import com.same.part.assistant.app.util.PhotoPickerUtil.RESULT_CODE_PHOTO_PICKER
import com.same.part.assistant.app.util.PhotoPickerUtil.choosePhoto
import com.same.part.assistant.app.util.PhotoPickerUtil.showPhotoPicker
import com.same.part.assistant.data.model.RequestShopCategoryInfo
import com.same.part.assistant.databinding.ActivityAddProductClassificationBinding
import com.same.part.assistant.viewmodel.request.RequestCategoryViewModel
import com.same.part.assistant.viewmodel.request.RequestUploadDataViewModel
import com.same.part.assistant.viewmodel.state.CategoryViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_add_product_classification.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions

/**
 * 添加商品分类
 */
class AddProductClassificationActivity :
    BaseActivity<CategoryViewModel, ActivityAddProductClassificationBinding>(),
    EasyPermissions.PermissionCallbacks {

    /**
     * 跳转来源
     */
    private lateinit var mJumpFromType: String

    private val mRequestCategoryViewModel: RequestCategoryViewModel by lazy { getViewModel<RequestCategoryViewModel>() }

    private val mRequestUploadDataViewModel: RequestUploadDataViewModel by lazy { getViewModel<RequestUploadDataViewModel>() }

    private lateinit var mParentId:String
    private lateinit var mCustomCategoryId:String

    override fun layoutId(): Int = R.layout.activity_add_product_classification

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        //跳转来源
        mJumpFromType = when (intent?.getStringExtra(JUMP_FROM_TYPE).orEmpty()) {
            JUMP_FROM_ADD_SECOND_CATEGORY -> {
                //添加二级标题
                mToolbarTitle.text = "添加商品分类"
                JUMP_FROM_ADD_SECOND_CATEGORY
            }
            else -> {
                //编辑标题
                mToolbarTitle.text = "编辑商品分类"
                JUMP_FROM_EDIT
            }
        }
        mParentId = intent?.getStringExtra(PARENT_ID).orEmpty()
        mCustomCategoryId = intent?.getStringExtra(CUSTOMCATEGORYID).orEmpty()

        //返回按钮
        mTitleBack.setOnClickListener { finish() }
        //编辑页面查询商品分类详情
        if (mJumpFromType == JUMP_FROM_EDIT) {
            mRequestCategoryViewModel.queryShopCategoryDetail(CacheUtil.getToken(), mCustomCategoryId)
        }
    }

    override fun createObserver() {
        //查询商品分类
        mRequestCategoryViewModel.queryShopCategoryDetailResult.observe(
            this,
            Observer { resultState ->
                parseStateResponseBody(resultState, {
                    val response: String = it.string()
                    val responseObject = JSON.parseObject(response)
                    val contentObject = responseObject.getJSONObject("content")
                    mViewModel.imageUrl.postValue(contentObject.getString("img") ?: "")
                    mViewModel.categoryName.postValue(contentObject.getString("name" ?: ""))
                    mViewModel.sequence.postValue(contentObject.getString("sequence") ?: "")
                    mViewModel.description.postValue(contentObject.getString("description") ?: "")
                })
            })
        //添加商品分类
        mRequestCategoryViewModel.addShopCategoryResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val jsonObject = JSON.parseObject(it.string())
                ToastUtils.showLong(jsonObject.getString("msg"))
                if (jsonObject.getIntValue("code") == 1) {
                    //返回上级页面刷新
                    EventBus.getDefault().post(ADDCLASSIFICATION_SUCCESS)
                    finish()
                }
            })
        })
        //编辑商品分类
        mRequestCategoryViewModel.editShopCategoryResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val jsonObject = JSON.parseObject(it.string())
                ToastUtils.showLong(jsonObject.getString("msg"))
                if (jsonObject.getIntValue("code") == 1) {
                    //返回上级页面刷新
                    EventBus.getDefault().post(ADDCLASSIFICATION_SUCCESS)
                    finish()
                }
            })
        })
        //七牛云
        mRequestUploadDataViewModel.uploadResult.observe(this, Observer { qiuniuModel ->
            when {
                qiuniuModel == null -> {//编辑时
                    val requestShopCategoryInfo = RequestShopCategoryInfo(
                        mViewModel.imageUrl.value,
                        mViewModel.categoryName.value,
                        mViewModel.sequence.value,
                        mViewModel.description.value,
                        mParentId
                    )
                    mRequestCategoryViewModel.editShopCategory("42", requestShopCategoryInfo)
                }
                qiuniuModel.qiniuResponseInfo != null -> {//七牛云上传失败

                }
                qiuniuModel.img.isNotEmpty() -> {//七牛云上传成功
                    val requestShopCategoryInfo = RequestShopCategoryInfo(
                        qiuniuModel.img,
                        mViewModel.categoryName.value,
                        mViewModel.sequence.value,
                        mViewModel.description.value,
                        mParentId
                    )
                    if (mJumpFromType == JUMP_FROM_EDIT) {//编辑商品
                        mRequestCategoryViewModel.editShopCategory(
                            mCustomCategoryId,
                            requestShopCategoryInfo
                        )
                    } else {//添加商品
                        mRequestCategoryViewModel.addShopCategory(
                            CacheUtil.getToken(),
                            requestShopCategoryInfo
                        )
                    }
                }
            }
        })
        //七牛云上传loading
        mRequestUploadDataViewModel.uploadingResult.observe(this, Observer {
            if (it.isNotEmpty()) {
                showLoading(it)
            }
        })
    }

    inner class ProxyClick {
        //选择头像
        fun chooseAvatar() {
            choosePhoto(this@AddProductClassificationActivity)
        }

        //保存
        fun save() {
            if (judgeCanSave()) {
                //添加/编辑商品分类(当编辑页面时没有去选择图片时不需要去七牛云上传)
                val isNeedUploadQiniu =
                    mJumpFromType == JUMP_FROM_EDIT && mViewModel.hasSelectPhoto.value
                mRequestUploadDataViewModel.uploadData(isNeedUploadQiniu, mViewModel.imageUrl.value)
            }
        }
    }

    private fun judgeCanSave(): Boolean = when {
        mViewModel.imageUrl.value.isEmpty() -> {
            ToastUtils.showShort("分类封面图不可为空！")
            false
        }
        mViewModel.categoryName.value.isEmpty()->{
            ToastUtils.showShort("分类名称不可为空！")
            false
        }
        mViewModel.sequence.value.isEmpty()->{
            ToastUtils.showShort("排序不可为空！")
            false
        }
        else -> true
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
                val imageUrl =
                    GlobalUtil.getRealFilePath(this@AddProductClassificationActivity, this[0]) ?: ""
                mViewModel.hasSelectPhoto.postValue(true)
                mViewModel.imageUrl.postValue(imageUrl)
                Glide.with(this@AddProductClassificationActivity)
                    .load(this[0])
                    .into(coverImg)
            }
        }
    }

    companion object {
        //跳转来源
        const val JUMP_FROM_TYPE = "JUMP_FROM_TYPE"

        //从添加二级跳转过来
        const val JUMP_FROM_ADD_SECOND_CATEGORY = "JUMP_FROM_ADD_SECOND_CATEGORY"

        //从编辑跳转过来
        const val JUMP_FROM_EDIT = "JUMP_FROM_EDIT"

        //一级时为空
        const val PARENT_ID = "PARENT_ID"

        //编辑id
        const val CUSTOMCATEGORYID = "CUSTOMCATEGORYID"

        const val ADDCLASSIFICATION_SUCCESS = "ADDCLASSIFICATION_SUCCESS"
    }
}