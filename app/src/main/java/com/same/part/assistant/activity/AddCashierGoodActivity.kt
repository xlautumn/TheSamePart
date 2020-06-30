package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.app.util.GlobalUtil
import com.same.part.assistant.app.util.NumberInputUtil
import com.same.part.assistant.app.util.PhotoPickerUtil
import com.same.part.assistant.app.util.PhotoPickerUtil.choosePhoto
import com.same.part.assistant.app.util.ScanBarCodeUtil
import com.same.part.assistant.data.model.*
import com.same.part.assistant.databinding.ActivityAddCashierGoodBinding
import com.same.part.assistant.viewmodel.request.RequestAddCashierGoodViewModel
import com.same.part.assistant.viewmodel.request.RequestUploadDataViewModel
import com.same.part.assistant.viewmodel.state.AddCashierGoodViewModel
import com.yzq.zxinglibrary.common.Constant
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_add_cashier_good.*
import kotlinx.android.synthetic.main.activity_add_cashier_good.coverImg
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import me.shaohui.bottomdialog.BottomDialog
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.EasyPermissions


/**
 * 添加收银商品
 */
class AddCashierGoodActivity :
    BaseActivity<AddCashierGoodViewModel, ActivityAddCashierGoodBinding>(),
    EasyPermissions.PermissionCallbacks {

    private var hasShowMessage = false

    /**
     * 跳转来源
     */
    private lateinit var mJumpFromType: String

    /**
     * 收银商品productId
     */
    private lateinit var mCashierEditId: String

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

    private val mRequestUploadDataViewModel: RequestUploadDataViewModel by lazy { getViewModel<RequestUploadDataViewModel>() }

    private val mRequestAddCashierGoodViewModel: RequestAddCashierGoodViewModel by lazy { getViewModel<RequestAddCashierGoodViewModel>() }

    override fun layoutId(): Int = R.layout.activity_add_cashier_good

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        //跳转来源
        mJumpFromType =
            when (intent?.getStringExtra(AddCashierGoodActivity.JUMP_FROM_TYPE).orEmpty()) {
                JUMP_FROM_ADD_CASHIER_GOOD -> {
                    //添加收银商品标题
                    mToolbarTitle.text = "添加商品"
                    JUMP_FROM_ADD_CASHIER_GOOD
                }
                else -> {
                    //编辑收银商品标题
                    mToolbarTitle.text = "编辑商品"
                    JUMP_FROM_EDIT
                }
            }
        //收银商品productId
        mCashierEditId = intent?.getStringExtra(CASHIER_PRODUCT_ID).orEmpty()

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
                                mViewModel.productCategoryId.postValue(productClassificationModel.id)
                            }
                            dialog.dismissAllowingStateLoss()
                        }
                    }

                }.setLayoutRes(R.layout.bottom_dialog_list).setDimAmount(0.4F)
                    .setCancelOutside(true).setTag("mChooseGoodClassification").show()
            } else {
                ToastUtils.showLong("暂无商品分类，请先添加商品分类")
            }
        }
        //是否是称重商品
        mChooseIfWeightGood.setOnClickListener {
            if (!mViewModel.editProductSku.value.isNullOrEmpty() && mSpecState.isChecked) {
                if (!hasShowMessage) {
                    hasShowMessage = true
                    showMessage(
                        "切换商品称重类型，将会清空已有的商品规格数据",
                        positiveButtonText = "确定",
                        positiveAction = {
                            hasShowMessage = false
                            showChooseIfWeightGoodDialog()
                            mViewModel.editProductSku.value = arrayListOf()
                        },
                        negativeButtonText = "取消",
                        negativeAction = {
                            hasShowMessage = false
                        }
                    )
                }
            } else {
                showChooseIfWeightGoodDialog()
            }

        }
        //多规格
        mSpecState.setOnTouchListener { v, event ->
            if (!mViewModel.editProductSku.value.isNullOrEmpty() && mSpecState.isChecked) {
                if (!hasShowMessage) {
                    hasShowMessage = true
                    showMessage(
                        "关闭多规格，将会清空已有的商品规格数据",
                        positiveButtonText = "确定",
                        positiveAction = {
                            hasShowMessage = false
                            mViewModel.editProductSku.value = arrayListOf()
                        },
                        negativeButtonText = "取消",
                        negativeAction = {
                            hasShowMessage = false
                        }
                    )
                }
                true
            } else {
                false
            }

        }
        mSpecState.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.specState.value = if (isChecked) 1 else 0
        }
        //选择计量单位
        mChooseUnit.setOnClickListener {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                //选择商品分类列表
                it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                    layoutManager = LinearLayoutManager(this@AddCashierGoodActivity)
                    val list =
                        if (mViewModel.type.value == mIfWeightGoodList[0]) mWeightGoodUnitList else mNotWeightGoodUnitList
                    adapter = CustomAdapter(list) { position ->
                        if (position >= 0 && position < list.size) {
                            mViewModel.unit.postValue(list[position])
                        }
                        dialog.dismissAllowingStateLoss()
                    }
                }

            }.setLayoutRes(R.layout.bottom_dialog_list).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("mChooseUnit").show()
        }
        //头像选择
        coverImg.setOnClickListener {
            choosePhoto(this)
        }
        //上架
        switch_button.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.shelvesState.postValue(if (isChecked) 1 else 0)
        }
        //保存
        saveCashier.setOnClickListener {
            if (judgeCanSave()) {
                //添加/编辑商品分类(当编辑页面时没有去选择图片时不需要去七牛云上传)
                val isNeedUploadQiniu =
                    (mJumpFromType == JUMP_FROM_EDIT && mViewModel.hasSelectPhoto.value) || mJumpFromType == JUMP_FROM_ADD_CASHIER_GOOD
                mRequestUploadDataViewModel.uploadData(isNeedUploadQiniu, mViewModel.imgs.value)
            }
        }
        //扫描二维码
        scanBarCode.setOnClickListener {
            ScanBarCodeUtil.startScanCode(this)
        }
        //库存
        mQuantity.digitsWithHintText(false)
        //加载商品分类数据
        mRequestAddCashierGoodViewModel.queryCashierGood(
            mJumpFromType == JUMP_FROM_EDIT,
            mCashierEditId
        )
        ll_product_spec.setOnClickListener {
            val intent = Intent(this, EditProductSpecActivity::class.java)
            intent.putExtra(
                EditProductSpecActivity.KEY_PRODUCT_TYPE,
                if (mViewModel.type.value == "是") {
                    EditProductSpecActivity.PRODUCT_TYPE_WEIGHT
                } else {
                    EditProductSpecActivity.PRODUCT_TYPE_NO_WEIGHT
                }
            )
            mViewModel.editProductSku.value?.takeIf { it.isNotEmpty() }?.let {
                intent.putExtra(EditProductSpecActivity.KEY_PRODUCT_SPEC, it)
            }
            startActivityForResult(
                intent,
                REQUEST_CODE_SPEC
            )
        }
    }
//
//    private fun checkSkuAndWeightType(): Boolean {
//        if (mViewModel.specState.value == 1) {
//            if (mViewModel.editProductSku.value.isNullOrEmpty()) {
//                return true
//            } else {
//                val isSkuWeight = mViewModel.editProductSku.value?.get(0)?.weight.isNullOrEmpty()
//                val isWeight = mViewModel.type.value == "是"
//                if (isWeight == isSkuWeight) {
//                    return true
//                } else {
//                    //                        showMessage("")
//                    //当前规格与商品称重类型不一致
//                    return false
//                }
//            }
//        } else {
//            return true
//        }
//    }

    override fun createObserver() {
        //商品分类名称数据
        mRequestAddCashierGoodViewModel.classificationnameListResult.observe(this, Observer {
            mClassificationNameList.addAll(it)
        })
        //商品分类列表数据
        mRequestAddCashierGoodViewModel.productClassificationListResult.observe(this, Observer {
            mProductClassificationList.addAll(it)
        })
        //详情数据
        mRequestAddCashierGoodViewModel.cashierGoodsDetailInfoResult.observe(
            this,
            Observer { resultState ->
                parseState(resultState, { cashDetailMode ->
                    mViewModel.imgs.postValue(cashDetailMode.img ?: "")
                    mViewModel.name.postValue(cashDetailMode.name ?: "")
                    mViewModel.productCategoryId.postValue(cashDetailMode.customCategoryProductId.toString())
                    mViewModel.type.postValue(if (cashDetailMode.type == "1") "是" else "否")
                    mViewModel.unit.postValue(cashDetailMode.unit ?: "")
                    mViewModel.price.postValue(cashDetailMode.price.toString())
                    mViewModel.sequence.postValue(cashDetailMode.sequence.toString())
                    mViewModel.shelvesState.postValue(cashDetailMode.state)
                    mViewModel.quantity.postValue(cashDetailMode.quantity)
                    mViewModel.editProductSku.postValue(ArrayList(cashDetailMode.productSkus))
                    mQuantity.digitsWithHintText(cashDetailMode.type == "1")
                    if (cashDetailMode.type == "1") {
                    } else {
                        mViewModel.barcode.postValue(cashDetailMode.barcode ?: "")
                    }
                    if (mProductClassificationList.size > 0) {
                        mProductClassificationList.forEach {
                            if (it.id == cashDetailMode.customCategoryProductId.toString()) {
                                selectedClassification.text = it.name
                            }
                        }
                    }
                })
            })

        mRequestAddCashierGoodViewModel.loadingChange.showDialog.observe(this, Observer {
            if (it.isNotEmpty()) {
                showLoading(it)
            }
        })

        //更新成功
        mRequestAddCashierGoodViewModel.createOrUpdateCashierGoodsResult.observe(
            this,
            Observer { resultState ->
                parseStateResponseBody(resultState, onSuccess = {
                    val jsonObject = JSON.parseObject(it.string())
                    if (jsonObject.getIntValue("code") == 1) {
                        ToastUtils.showLong(jsonObject.getString("msg"))
                        //返回上级页面刷新
                        EventBus.getDefault().post(ADD_OR_UPDATE_CASHIER_SUCCESS)
                        finish()
                    } else {
                        ToastUtils.showLong(jsonObject.getString("message"))
                    }
                }, onError = {
                    ToastUtils.showLong(it.errorMsg)
                })
            })
        //七牛云
        mRequestUploadDataViewModel.uploadResult.observe(this, Observer { qiuniuModel ->
            when {
                qiuniuModel == null -> {//编辑时
                    createOrUpdateCashierGood(mViewModel.imgs.value)
                }
                qiuniuModel.img.isNotEmpty() -> { //七牛云上传成功
                    createOrUpdateCashierGood(qiuniuModel.img)
                }
                qiuniuModel.qiniuResponseInfo != null -> {//七牛云上传失败
                    dismissLoading()
                    ToastUtils.showLong("图片上传失败")
                }
            }
        })
        //七牛云上传loading
        mRequestUploadDataViewModel.uploadingResult.observe(this, Observer {
            if (it.isNotEmpty()) {
                showLoading(it)
            }
        })
        mViewModel.specState.observe(this, Observer {
            mScannerBarcode.visibility = if (it == 0 && mViewModel.type.value == "否") {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
        mViewModel.type.observe(this, Observer {
            mScannerBarcode.visibility = if (it == "否" && mViewModel.specState.value == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        mViewModel.editProductSku.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                mSpecState.isChecked = false
                tv_spec.text = "未设置，点击设置"
            } else {
                mSpecState.isChecked = true
                tv_spec.text = "已设置，点击查看"
            }
        })

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
        } else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA_FOR_SCAN_BARCODE) {
            ToastUtils.showShort("扫描商品条形码需要访问您的存储权限和照相机权限,请在手机设置中打开以上权限")
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == PhotoPickerUtil.REQUEST_CODE_EXTERNAL_STORAGE_AND_CAMERA) {
            PhotoPickerUtil.showPhotoPicker(this)
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
            //选头像
            if (requestCode == PhotoPickerUtil.RESULT_CODE_PHOTO_PICKER) {
                Matisse.obtainResult(data)?.takeIf { it.isNotEmpty() }?.apply {
                    val imageUrl =
                        GlobalUtil.getRealFilePath(this@AddCashierGoodActivity, this[0]).orEmpty()
                    mViewModel.imgs.postValue(imageUrl)
                    mViewModel.hasSelectPhoto.postValue(true)
                    Glide.with(this@AddCashierGoodActivity)
                        .load(this[0])
                        .into(coverImg)
                }
            }
            //扫商品条形码
            else if (requestCode == ScanBarCodeUtil.REQUEST_CODE_SCAN) {
                val content = data?.getStringExtra(Constant.CODED_CONTENT).orEmpty()
                barCode.setText(content)
            } else if (requestCode == REQUEST_CODE_SPEC) {
                val specList =
                    data?.getSerializableExtra(KEY_SETTING_PRODUCT_SPEC) as? ArrayList<ProductSkuV2>
                mViewModel.editProductSku.value = specList
            }
        }
    }

    inner class CustomAdapter(var dataList: ArrayList<String>, var onItemClick: (Int) -> Unit) :
        RecyclerView.Adapter<ChooseItemHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ChooseItemHolder =
            ChooseItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.choose_list_item,
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

    /**
     * 判断输入
     */
    private fun judgeCanSave(): Boolean = when {
//        mViewModel.imgs.value.isEmpty() -> {
//            ToastUtils.showShort("商品缩略图不可为空！")
//            false
//        }
        mViewModel.name.value.isEmpty() -> {
            ToastUtils.showShort("商品名称不可为空！")
            false
        }
        mViewModel.productCategoryId.value.isEmpty() -> {
            ToastUtils.showShort("商品分类不可为空！")
            false
        }

        mViewModel.price.value.isEmpty() -> {
            ToastUtils.showShort("单价不可为空！")
            false
        }
        mViewModel.specState.value == 0 && mViewModel.quantity.value.isEmpty() -> {
            ToastUtils.showShort("库存不可为空！")
            false
        }
        mViewModel.specState.value == 0 && mViewModel.quantity.value.contains(".") && mViewModel.type.value != "是" -> {
            ToastUtils.showShort("非称重商品库存不能包含小数点！")
            false
        }

        mViewModel.sequence.value.isEmpty() -> {
            ToastUtils.showShort("排序不可为空！")
            false
        }

        mViewModel.specState.value == 1 && mViewModel.editProductSku.value.isNullOrEmpty() -> {
            ToastUtils.showShort("商品规格不能为空")
            false
        }
//        mViewModel.productCategoryId.value.isEmpty() && mViewModel.type.value == "否"-> {
//            ToastUtils.showShort("商品条码不可为空！")
//            false
//        }
        else -> true
    }

    /**
     * 更新/添加商品
     */
    private fun createOrUpdateCashierGood(img: String) {
        if (mViewModel.specState.value == 0) {
            val requestShopCategoryInfo = CreateOrUpdateGoodsInfo(
                mViewModel.barcode.value,
                img,
                mViewModel.name.value,
                mViewModel.price.value,
                mViewModel.productCategoryId.value,
                mViewModel.sequence.value,
                if (mViewModel.type.value == "是") "1" else "2",
                mViewModel.unit.value,
                mViewModel.quantity.value,
                state = mViewModel.shelvesState.value
            )
            if (mJumpFromType == JUMP_FROM_EDIT) {//编辑商品
                requestShopCategoryInfo.id = mCashierEditId
            }
            mRequestAddCashierGoodViewModel.createOrUpdateCashierGood(requestShopCategoryInfo)
        } else {
            val requestCreateProduct = RequestCreateProduct(
                mViewModel.barcode.value.takeUnless { it.isNullOrEmpty() },//不需要条形码时，传null
                img,
                mViewModel.name.value,
                mViewModel.price.value,
                mViewModel.productCategoryId.value,
                mViewModel.sequence.value,
                if (mViewModel.type.value == "是") "1" else "2",
                mViewModel.unit.value,
                mViewModel.quantity.value,
                state = mViewModel.shelvesState.value,
                productSkus = if (mViewModel.specState.value == 1) mViewModel.editProductSku.value else null
            )
            if (mJumpFromType == JUMP_FROM_EDIT) {//编辑商品
                mRequestAddCashierGoodViewModel.updateProduct(mCashierEditId, requestCreateProduct)
            } else {
                mRequestAddCashierGoodViewModel.createProduct(requestCreateProduct)
            }
        }
    }

    /**
     * 库存hint显示以及小数点位数
     */
    private fun EditText.digitsWithHintText(hasDigits: Boolean) {
        if (hasDigits) {
            hint = "称重商品库存为商品总重量"
            NumberInputUtil.setPriceMode(this, 3)
        } else {
            hint = "非称重商品库存为数量"
            inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    /**
     * 选择是否是称重商品Dialog
     */
    private fun showChooseIfWeightGoodDialog() {
        val dialog = BottomDialog.create(supportFragmentManager)
        dialog.setViewListener {
            //选择商品分类列表
            it.findViewById<RecyclerView>(R.id.recyclerview).apply {
                layoutManager = LinearLayoutManager(this@AddCashierGoodActivity)
                adapter = CustomAdapter(mIfWeightGoodList) { position ->
                    if (position >= 0 && position < mIfWeightGoodList.size) {
                        val option = mIfWeightGoodList[position]
                        mViewModel.type.postValue(option)
                        selectedIfWeightGood.text = option
                        //库存
                        mQuantity.digitsWithHintText(position == 0)
                        //此时选择的是称重商品
                        if (position == 0) {
                            mViewModel.unit.postValue(mWeightGoodUnitList[0])
                        } else {
                            mViewModel.unit.postValue(mNotWeightGoodUnitList[0])
                        }
                    }
                    dialog.dismissAllowingStateLoss()
                }
            }

        }.setLayoutRes(R.layout.bottom_dialog_list).setDimAmount(0.4F)
            .setCancelOutside(true).setTag("mChooseIfWeightGood").show()
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

        //添加收银商品成功
        const val ADD_OR_UPDATE_CASHIER_SUCCESS = "ADD_CASHIER_SUCCESS"

        //收银商品productId
        const val CASHIER_PRODUCT_ID = "CASHIER_PRODUCT_ID"

        /**
         * 多规格设置的数据
         */
        const val KEY_SETTING_PRODUCT_SPEC = "KEY_SETTING_PRODUCT_SPEC"

        /**
         * 设置多规格
         */
        const val REQUEST_CODE_SPEC = 10
    }
}