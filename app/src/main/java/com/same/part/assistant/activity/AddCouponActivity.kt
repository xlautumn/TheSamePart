package com.same.part.assistant.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.util.DatetimeUtil
import com.same.part.assistant.app.util.DatetimeUtil.now
import com.same.part.assistant.app.util.NumberInputUtil
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.RequestCreateCouponInfo
import com.same.part.assistant.databinding.ActivityAddCouponBinding
import com.same.part.assistant.viewmodel.request.RequestCreateCouponViewModel
import com.same.part.assistant.viewmodel.state.CreateCouponViewModel
import com.same.part.assistant.viewmodel.state.CreateCouponViewModel.Companion.RANGE_TYPE_ALL
import com.same.part.assistant.viewmodel.state.CreateCouponViewModel.Companion.RANGE_TYPE_PART_IN
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import me.shaohui.bottomdialog.BottomDialog
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

/**
 * 添加会员卡
 */
class AddCouponActivity :
    BaseActivity<CreateCouponViewModel, ActivityAddCouponBinding>() {

    private val mRequestCreateCouponViewModel: RequestCreateCouponViewModel by lazy { getViewModel<RequestCreateCouponViewModel>() }

    override fun layoutId(): Int = R.layout.activity_add_coupon

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        //标题
        mToolbarTitle.text = "添加优惠券"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
    }

    override fun createObserver() {
        //添加优惠券
        mRequestCreateCouponViewModel.saveResult.observe(this, Observer { resultState ->
            parseStateResponseBody(resultState, {
                val jsonObject = JSON.parseObject(it.string())
                val code = jsonObject.getIntValue("code")
                ToastUtils.showLong(jsonObject.getString("msg"))
                if (code == 1) {
                    //保存成功
                    EventBus.getDefault().post(ADD_COUPON_SUCCESS)
                    finish()
                }
            })
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHANNEL_NUM_SETTING) {
                mViewModel.couponReceiveChannel.set(
                    data?.getIntExtra(ChannelActivity.LITMIT_NUM, 1) ?: 1
                )
            } else if (requestCode == REQUEST_CODE_SUITABLE_PRODUCT) {
                val list =
                    data?.getSerializableExtra(SuitableProductActivity.KEY_SUITABLE_PRODUCT_LIST) as? ArrayList<CashierProduct>
                mViewModel.rangeValue = list
            }
        }
    }

    /** 开始时间与结束*/
    private val mStartTimeDate = DatePickerInfo()
    private val mEndTimeDate = DatePickerInfo()

    class DatePickerInfo() {
        val ca = Calendar.getInstance()
        var year: Int = ca[Calendar.YEAR]
        var month = ca[Calendar.MONTH]
        var day = ca[Calendar.DAY_OF_MONTH]
    }

    inner class ProxyClick {
        //保存
        fun save() {
            if (checkOptions()) {
                val requestCreateCouponInfo = RequestCreateCouponInfo(
                    mViewModel.name.get(),
                    mViewModel.distributionTotal.get().toInt(),
                    mViewModel.usingThreshold.get().toDouble(),
                    mViewModel.creditAmount.get().toDouble(),
                    mViewModel.couponReceiveChannel.get(),
                    mViewModel.endTime.get(),
                    mViewModel.startTime.get(),
                    mViewModel.rangeType.get(),
                    mViewModel.rangeValue?.map { it.productId }
                )
                mRequestCreateCouponViewModel.createCouponActivity(requestCreateCouponInfo)
            }
        }

        //选择时间
        fun chooseTime(isStartTime: Boolean) {
            val dataPick = if (isStartTime) mStartTimeDate else mEndTimeDate
            DatePickerDialog(
                this@AddCouponActivity,
                OnDateSetListener { _, year, month, dayOfMonth ->
                    dataPick.year = year
                    dataPick.month = month
                    dataPick.day = dayOfMonth
                    val time =
                        "${year}-${month.mendZero(true)}-${dayOfMonth.mendZero(false)} ${DatetimeUtil.formatDate(
                            now,
                            DatetimeUtil.DATE_PATTERN_HH_MM_SS
                        )}"
                    if (isStartTime) {
                        mViewModel.startTime.set(time)
                    } else {
                        mViewModel.endTime.set(time)
                    }
                },
                dataPick.year, dataPick.month, dataPick.day
            ).apply {
                datePicker.minDate = Calendar.getInstance().timeInMillis
                show()
            }
        }

        //使用门槛
        fun chooseUsingThreshold() {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                handleThresholdDialogView(it, dialog)
            }.setLayoutRes(R.layout.dialog_use_threshold).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("mChooseUsingThreshold").show()
        }

        /**
         * 领取渠道
         */
        fun pickUpChannel() {
            startActivityForResult(
                Intent(
                    this@AddCouponActivity,
                    ChannelActivity::class.java
                ).apply {
                    putExtra(ChannelActivity.LITMIT_NUM, mViewModel.couponReceiveChannel.get())
                }, CHANNEL_NUM_SETTING
            )
        }

        /**
         * 选择使用商品
         */
        fun suitableProduct() {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                handleSuitableProductDialogView(it, dialog)
            }.setLayoutRes(R.layout.dialog_select_suitable_product).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("suitableProduct").show()
        }
    }

    /**
     * 数字小于10前面补0
     */
    fun Int.mendZero(needPlus: Boolean): String {
        val i = if (needPlus) this + 1 else this
        return if (i < 10) {
            "0$i"
        } else {
            "$i"
        }
    }

    /**
     * 检查是否填写
     */
    private fun checkOptions(): Boolean {
        if (mViewModel.name.get().isEmpty()) {
            ToastUtils.showShort("请填写名称")
            return false
        }
        if (mViewModel.distributionTotal.get().isEmpty()) {
            ToastUtils.showShort("请填写发送总量")
            return false
        }
        if (mViewModel.creditAmount.get().isEmpty()) {
            ToastUtils.showShort("请填写见面金额")
            return false
        }

        if (mViewModel.startTime.get().isEmpty()) {
            ToastUtils.showShort("请填写用券开始时间")
            return false
        }
        if (mViewModel.endTime.get().isEmpty()) {
            ToastUtils.showShort("请填写用券结束时间")
            return false
        }
        val isVaildTime =
            DatetimeUtil.dateToStamp(mViewModel.endTime.get()) < DatetimeUtil.dateToStamp(mViewModel.startTime.get())
        if (isVaildTime) {
            ToastUtils.showShort("用券结束时间请大于用券开始时间")
            return false
        }

        if (mViewModel.rangeType.get() == RANGE_TYPE_PART_IN && mViewModel.rangeValue.isNullOrEmpty()) {
            ToastUtils.showShort("请选择适用商品")
            return false
        }

        return true
    }

    /**
     * 使用门槛
     */
    private fun handleThresholdDialogView(
        it: View,
        dialog: BottomDialog
    ) {
        val ivNoThreshold = it.findViewById<ImageView>(R.id.ivNoThreshold)
        val ivHasThreshold = it.findViewById<ImageView>(R.id.ivHasThreshold)
        val etThresholdNum = it.findViewById<EditText>(R.id.etThresholdNum)
        NumberInputUtil.setPriceMode(etThresholdNum, 2)
        if (mViewModel.usingThreshold.get() == "0") {
            ivNoThreshold.isSelected = true
            ivHasThreshold.isSelected = false
        } else {
            ivNoThreshold.isSelected = false
            ivHasThreshold.isSelected = true
            etThresholdNum.setText(mViewModel.usingThreshold.get())
        }
        it.findViewById<LinearLayout>(R.id.llNoThreshold).setOnClickListener {
            ivNoThreshold.isSelected = true
            ivHasThreshold.isSelected = false
        }
        it.findViewById<LinearLayout>(R.id.llHasThreshold).setOnClickListener {
            ivNoThreshold.isSelected = false
            ivHasThreshold.isSelected = true
        }
        it.findViewById<TextView>(R.id.tvConfirm).setOnClickListener {
            if (etThresholdNum.text.isEmpty() && ivHasThreshold.isSelected) {
                ToastUtils.showLong("请填写金额")
                return@setOnClickListener
            }
            mViewModel.usingThreshold.set(if (ivNoThreshold.isSelected) "0" else etThresholdNum.text.toString())
            dialog.dismissAllowingStateLoss()
        }
    }

    private fun handleSuitableProductDialogView(view: View, dialog: BottomDialog) {
        val radioAllProduct = view.findViewById<RadioButton>(R.id.radio_all_product)
        val radioPartProduct = view.findViewById<RadioButton>(R.id.radio_part_product)
        if (mViewModel.rangeType.get() == RANGE_TYPE_ALL) {
            radioAllProduct.isChecked = true
        } else {
            radioPartProduct.isChecked = true
        }
        view.findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            when {
                radioAllProduct.isChecked -> {
                    mViewModel.rangeType.set(RANGE_TYPE_ALL)
                    mViewModel.rangeValue = null
                    mDatabind.tvSuitableProduct.text = "全部商品"
                }
                radioPartProduct.isChecked -> {
                    mViewModel.rangeType.set(RANGE_TYPE_PART_IN)
                    mDatabind.tvSuitableProduct.text = "部分商品"
                    startActivityForResult(Intent(this,SuitableProductActivity::class.java).putExtra(
                        SuitableProductActivity.KEY_SUITABLE_PRODUCT_LIST,mViewModel.rangeValue
                    ),REQUEST_CODE_SUITABLE_PRODUCT)
                }
            }
            dialog.dismissAllowingStateLoss()
        }
    }

    companion object {
        /** 添加优惠券成功*/
        const val ADD_COUPON_SUCCESS = "ADD_COUPON_SUCCESS"

        /** 领取渠道*/
        const val CHANNEL_NUM_SETTING = 1000

        const val REQUEST_CODE_SUITABLE_PRODUCT = 200
    }


}