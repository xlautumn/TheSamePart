package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.app.util.PhotoPickerUtil
import com.same.part.assistant.app.util.PhotoPickerUtil.choosePhoto
import com.same.part.assistant.app.util.ScanBarCodeUtil
import com.same.part.assistant.data.model.ProductClassificationModel
import com.same.part.assistant.utils.HttpUtil
import com.yzq.zxinglibrary.common.Constant
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_add_cashier_good.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.shaohui.bottomdialog.BottomDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * 添加收银商品
 */
class AddCashierGoodActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    /**
     * 跳转来源
     */
    private lateinit var mJumpFromType: String

    /**
     * 商品分类列表数据
     */
    private val mProductClassificationList = ArrayList<ProductClassificationModel>()

    /**
     * 是否是称重商品列表数据
     */
    private val mIfWeightGoodList = arrayListOf("是", "否")

    /**
     * 非称重商品单位列表
     */
    private val mNotWeightGoodUnitList = arrayListOf("个", "支", "瓶", "袋", "盒")

    /**
     * 称重商品单位列表
     */
    private val mWeightGoodUnitList = arrayListOf("kg")

    /**
     * 商品分类名称数据
     */
    private val mClassificationNameList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cashier_good)

        //跳转来源
        mJumpFromType =
            when (intent?.getStringExtra(AddCashierGoodActivity.JUMP_FROM_TYPE).orEmpty()) {
                JUMP_FROM_ADD_CASHIER_GOOD -> {
                    //添加收银商品标题
                    mToolbarTitle.text = "添加收银商品"
                    JUMP_FROM_ADD_CASHIER_GOOD
                }
                else -> {
                    //编辑收银商品标题
                    mToolbarTitle.text = "编辑收银商品"
                    JUMP_FROM_EDIT
                }
            }
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //选择商品分类
        mChooseGoodClassification.setOnClickListener {
            if (mClassificationNameList.size > 0) {
                val dialog = BottomDialog.create(supportFragmentManager)
                dialog.setViewListener {
                    //选择商品分类列表
                    it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                        layoutManager = LinearLayoutManager(this@AddCashierGoodActivity)
                        adapter = CustomAdapter(mClassificationNameList) { position ->
                            if (mProductClassificationList.size > 0 && position >= 0 && position < mProductClassificationList.size) {
                                val productClassificationModel =
                                    mProductClassificationList[position]
                                selectedClassification.text = productClassificationModel.name
                            }
                            dialog.dismissAllowingStateLoss()
                        }
                    }

                }.setLayoutRes(R.layout.add_cashier_bottom_dialog).setDimAmount(0.1F)
                    .setCancelOutside(true).setTag("mChooseGoodClassification").show()
            }
        }
        //是否是称重商品
        mChooseIfWeightGood.setOnClickListener {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                //选择商品分类列表
                it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                    layoutManager = LinearLayoutManager(this@AddCashierGoodActivity)
                    adapter = CustomAdapter(mIfWeightGoodList) { position ->
                        if (position >= 0 && position < mIfWeightGoodList.size) {
                            val option = mIfWeightGoodList[position]
                            selectedIfWeightGood.text = option
                            //此时选择的是称重商品
                            if (position == 0) {
                                selectedUnit.text = mWeightGoodUnitList[0]
                                mScannerBarcode.visibility = View.GONE
                            } else {
                                selectedUnit.text = mNotWeightGoodUnitList[0]
                                mScannerBarcode.visibility = View.VISIBLE
                            }
                        }
                        dialog.dismissAllowingStateLoss()
                    }
                }

            }.setLayoutRes(R.layout.add_cashier_bottom_dialog).setDimAmount(0.1F)
                .setCancelOutside(true).setTag("mChooseIfWeightGood").show()
        }
        //选择计量单位
        mChooseUnit.setOnClickListener {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                //选择商品分类列表
                it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                    layoutManager = LinearLayoutManager(this@AddCashierGoodActivity)
                    val list =
                        if (selectedIfWeightGood.text == mIfWeightGoodList[0]) mWeightGoodUnitList else mNotWeightGoodUnitList
                    adapter = CustomAdapter(list) { position ->
                        if (position >= 0 && position < list.size) {
                            selectedUnit.text = list[position]
                        }
                        dialog.dismissAllowingStateLoss()
                    }
                }

            }.setLayoutRes(R.layout.add_cashier_bottom_dialog).setDimAmount(0.1F)
                .setCancelOutside(true).setTag("mChooseUnit").show()
        }
        //头像选择
        coverImg.setOnClickListener {
            choosePhoto(this)
        }
        //扫描二维码
        scanBarCode.setOnClickListener {
            ScanBarCodeUtil.startScanCode(this)
        }
        //加载商品分类数据
        loadProductClassificationList(page = 0)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
            PhotoPickerUtil.showPhotoPicker(this)
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
            //选头像
            if (requestCode == PhotoPickerUtil.RESULT_CODE_PHOTO_PICKER) {
                Matisse.obtainResult(data)?.takeIf { it.isNotEmpty() }?.apply {
                    val imageUrl =
                        GlobalUtil.getRealFilePath(this@AddCashierGoodActivity, this[0]).orEmpty()
                    Glide.with(this@AddCashierGoodActivity)
                        .load(this[0])
                        .into(coverImg)
                }
            }
            //扫商品条形码
            else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_SCAN) {
                val content = data?.getStringExtra(Constant.CODED_CONTENT).orEmpty()
                barCode.setText(content)
            }
        }
    }

    /**
     * 请求商品分类列表
     */
    private fun loadProductClassificationList(
        name: String = "",
        page: Int,
        size: String = "${Int.MAX_VALUE}"
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}custom-categories")
            .append("?page=$page")
            .append("&name=$name")
            .append("&size=$size")
            .append(
                "&shopId=${CacheUtil.getShopUserModel()?.UserShopDTO?.takeIf { it.size > 0 }?.get(0)?.shop?.shopId}"
            )
            .append("&appKey=${CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appKey}")
            .append("&appSecret=${CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appSecret}")
        HttpUtil.instance.getUrlWithHeader("WSCX", CacheUtil.getToken(), url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.apply {
                    getJSONArray("content")?.takeIf { it.size >= 0 }?.apply {
                        if (this.size == 0) {
                            ToastUtils.showShort("暂无商品分类，无法添加！")
                            finish()
                        } else {
                            val itemList = ArrayList<ProductClassificationModel>()
                            val nameList = ArrayList<String>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    val id = getString("customCategoryId")
                                    val name = getString("name")
                                    val parentId =
                                        getJSONObject("parent")?.getString("customCategoryId")
                                            .orEmpty()
                                    //过滤掉一级分类
                                    if (parentId.isNotEmpty()) {
                                        ProductClassificationModel(id, name, parentId).apply {
                                            itemList.add(this)
                                        }
                                        nameList.add(name)
                                    }
                                }
                            }
                            mProductClassificationList.addAll(itemList)
                            mClassificationNameList.addAll(nameList)
                            if (mProductClassificationList.size == 0) {
                                ToastUtils.showShort("暂无商品分类，无法添加！")
                                finish()
                            }
                        }
                    } ?: also {
                        ToastUtils.showShort("暂无商品分类，无法添加！")
                        finish()
                    }
                }
            } catch (e: Exception) {
                ToastUtils.showShort("暂无商品分类，无法添加！")
                finish()
            }
        }, {
            ToastUtils.showShort("商品分类请求失败，请稍后再试！")
            finish()
        })
    }

    inner class CustomAdapter(var dataList: ArrayList<String>, var onItemClick: (Int) -> Unit) :
        RecyclerView.Adapter<ChooseItemHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ChooseItemHolder =
            ChooseItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.choose_cashier_good_classification_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: ChooseItemHolder, position: Int) {
            holder.option.apply {
                text = dataList[position]
                setOnClickListener {
                    onItemClick.invoke(position)
                }
            }
        }
    }

    class ChooseItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var option: TextView = itemView.findViewById(R.id.option)
    }

    companion object {
        //跳转来源
        const val JUMP_FROM_TYPE = "JUMP_FROM_TYPE"

        //从添加二级跳转过来
        const val JUMP_FROM_ADD_CASHIER_GOOD = "JUMP_FROM_ADD_CASHIER_GOOD"

        //从编辑跳转过来
        const val JUMP_FROM_EDIT = "JUMP_FROM_EDIT"
    }
}