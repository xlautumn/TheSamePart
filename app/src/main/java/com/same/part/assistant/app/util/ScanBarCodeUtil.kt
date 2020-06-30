package com.same.part.assistant.app.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.same.part.assistant.app.util.PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA
import com.yzq.zxinglibrary.android.CaptureActivity
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


object ScanBarCodeUtil {

    //请求SD卡权限和CAMERA请求码
    const val REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE = 30

    //请求扫码
    const val REQUEST_CODE_SCAN = 40

    //需要请求的权限
    val PERMISSIONS_REQUEST_LIST = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    /**
     * 启动扫码
     */
    fun startScanCode(activity: Activity) {
        if (EasyPermissions.hasPermissions(activity, *PERMISSIONS_REQUEST_LIST)) {
            scannerBarCode(activity)
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    activity,
                    REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE,
                    *PERMISSIONS_REQUEST_LIST
                ).setRationale("扫描商品条形码需要访问您的存储权限和照相机权限。").build()
            )
        }
    }
    /**
     * 启动扫码
     */
    fun startScanCode(fragment: Fragment) {
        fragment.context?.let {
            if (EasyPermissions.hasPermissions(it, *PERMISSIONS_REQUEST_LIST)) {
                scannerBarCode(fragment)
            } else {
                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(
                        fragment,
                        REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE,
                        *PERMISSIONS_REQUEST_LIST
                    ).setRationale("扫描商品条形码需要访问您的存储权限和照相机权限。").build()
                )
            }
        }

    }
    /**
     * 展示扫码页面
     */
    fun scannerBarCode(activity: Activity) {
        val intent = Intent(activity, CaptureActivity::class.java)
        activity.startActivityForResult(intent, REQUEST_CODE_SCAN)
    }
    /**
     * 展示扫码页面
     */
    fun scannerBarCode(fragment: Fragment) {
        val intent = Intent(fragment.context, CaptureActivity::class.java)
        fragment.startActivityForResult(intent, REQUEST_CODE_SCAN)
    }
}