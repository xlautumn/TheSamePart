package com.same.part.assistant.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_shop_manager.*
import kotlinx.android.synthetic.main.fragment_home.userAvatar
import kotlinx.android.synthetic.main.toolbar_title.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

/**
 * 店铺管理
 */
class ShopManagerActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_manager)
        Glide.with(this)
            .load("https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108")
            .into(userAvatar)
        //标题
        mToolbarTitle.text = "店铺管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //选择头像
        shopAvatar.setOnClickListener {
            if (EasyPermissions.hasPermissions(this, *PERMISSIONS_REQUEST_LIST)) {
                showPhotoPicker()
            } else {
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(
                        this,
                        REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA, *PERMISSIONS_REQUEST_LIST
                    ).setRationale("设置头像需要访问您的存储权限和照相机权限。").build()
                )
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