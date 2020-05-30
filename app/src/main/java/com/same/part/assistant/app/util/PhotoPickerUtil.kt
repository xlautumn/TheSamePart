package com.same.part.assistant.app.util

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

object PhotoPickerUtil {

    //请求SD卡权限和CAMERA请求码
    const val REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA = 1

    //选择图片的结果
    const val RESULT_CODE_PHOTO_PICKER = 2

    //FileProvider
    private const val FILE_PROVIDER_AUTHORITY = "com.same.part.assistant.fileProvider"

    //需要请求的权限
    val PERMISSIONS_REQUEST_LIST = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    /**
     * 选择头像
     */
    fun choosePhoto(activity:Activity) {
        if (EasyPermissions.hasPermissions(activity, *PERMISSIONS_REQUEST_LIST)) {
            showPhotoPicker(activity)
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(activity, REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA, *PERMISSIONS_REQUEST_LIST)
                    .setRationale("设置头像需要访问您的存储权限和照相机权限。").build()
            )
        }
    }

    /**
     * 显示头像
     */
    fun showPhotoPicker(activity:Activity) {
        Matisse.from(activity).choose(MimeType.ofImage()).showSingleMediaType(true).capture(true)
            .captureStrategy(CaptureStrategy(true, FILE_PROVIDER_AUTHORITY))
            .countable(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT).thumbnailScale(0.85f)
            .imageEngine(GlideEngine()).showPreview(true).forResult(RESULT_CODE_PHOTO_PICKER)
    }
}