package com.same.part.assistant.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.qiniu.android.http.ResponseInfo
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.setCanInput
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.databinding.ActivityShopManagerBinding
import com.same.part.assistant.utils.QiniuManager
import com.same.part.assistant.viewmodel.request.RequestShopManagerViewModel
import com.same.part.assistant.viewmodel.state.ShopManagerViewModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_shop_manager.*
import kotlinx.android.synthetic.main.fragment_home.userAvatar
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.util.*

/**
 * 店铺管理
 */
class ShopManagerActivity : BaseActivity<ShopManagerViewModel, ActivityShopManagerBinding>(),
    EasyPermissions.PermissionCallbacks {

    private val mRequestShopManagerViewModel: RequestShopManagerViewModel by lazy { getViewModel<RequestShopManagerViewModel>() }

    /**
     * GIF图片的本地完整路径。
     */
    private var gifPath: String = ""


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
        mRequestShopManagerViewModel.shopResult.observe(this, Observer { resultState ->
            parseState(resultState, {
                mViewModel.imageUrl.postValue(it.img?:"")
                mViewModel.shopName.postValue(it.name ?: "")
                mViewModel.shopDesc.postValue(it.brand ?: "")
                mViewModel.operatorName.postValue(it.linkman ?: "")
                mViewModel.operatorPhone.postValue(it.mobile ?: "")
                mViewModel.shopId.postValue(it.shopId.toString())
                mViewModel.address.postValue(
                    it.province.plus(it.city).plus(it.district).plus(it.address)
                )
            })
        })

        mRequestShopManagerViewModel.qiniuTokenResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val response: String = it.string()
                val jsonObject = JSON.parseObject(response)
                val contentJsonObject = jsonObject.getJSONObject("content")
                val qiniuToken = contentJsonObject.getString("upToken")
                uploadQiniu(qiniuToken)
            })
        })

        mRequestShopManagerViewModel.updateResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                ToastUtils.showLong("更新成功")
//                val response: String = it.string()
//                val jsonObject = JSON.parseObject(response)
//                ToastUtils.showLong(jsonObject.getJSONObject("msg").toString())
            })
        })
    }

    inner class ProxyClick {
        //选择头像
        fun chooseAvatar() {
            if (EasyPermissions.hasPermissions(
                    this@ShopManagerActivity,
                    *PERMISSIONS_REQUEST_LIST
                )
            ) {
                showPhotoPicker()
            } else {
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(
                        this@ShopManagerActivity,
                        REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA, *PERMISSIONS_REQUEST_LIST
                    ).setRationale("设置头像需要访问您的存储权限和照相机权限。").build()
                )
            }
        }

        fun save() {
            mRequestShopManagerViewModel.getQiniuToken(CacheUtil.getToken())
        }

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
            showPhotoPicker()
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
                gifPath = GlobalUtil.getRealFilePath(this@ShopManagerActivity, this[0]) ?: ""
                Glide.with(this@ShopManagerActivity)
                    .load(this[0])
                    .into(userAvatar)
            }
        }
    }

    /**
     * 选择头像
     */
    private fun showPhotoPicker() {
        Matisse.from(this).choose(MimeType.ofImage()).showSingleMediaType(true).capture(true)
            .captureStrategy(CaptureStrategy(true, FILE_PROVIDER_AUTHORITY))
            .countable(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).thumbnailScale(0.85f)
            .imageEngine(GlideEngine()).showPreview(true).forResult(RESULT_CODE_PHOTO_PICKER)
    }

    /**
     * 七牛云上传
     */
    private fun uploadQiniu(qiniuToken: String) {
        val key = GlobalUtil.generateKey(gifPath, "portrait")
        QiniuManager.upload(gifPath, key, qiniuToken, object : QiniuManager.UploadListener {
            override fun onSuccess(key: String) {
                mRequestShopManagerViewModel.saveEditContent(
                    CacheUtil.getToken(),
                    QiniuManager.getImgUrl(key),
                    editShopName.text.toString(),
                    editDesc.text.toString(),
                    mViewModel.shopId.value
                )
            }

            override fun onFailure(info: ResponseInfo?) {
            }

            override fun onProgress(percent: Double) {
            }
        })
    }

    companion object {
        //请求SD卡权限和CAMERA请求码
        const val REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA = 1

        //选择图片的结果
        const val RESULT_CODE_PHOTO_PICKER = 2

        //FileProvider
        const val FILE_PROVIDER_AUTHORITY = "com.same.part.assistant.fileProvider"

        //需要请求的权限
        val PERMISSIONS_REQUEST_LIST = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }
}