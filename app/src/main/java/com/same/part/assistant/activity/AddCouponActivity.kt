package com.same.part.assistant.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.util.DatetimeUtil
import com.same.part.assistant.app.util.DatetimeUtil.now
import com.same.part.assistant.app.util.NumberInputUtil
import com.same.part.assistant.data.model.RequestCreateCouponInfo
import com.same.part.assistant.databinding.ActivityAddCouponBinding
import com.same.part.assistant.viewmodel.request.RequestCreateCouponViewModel
import com.same.part.assistant.viewmodel.state.CreateCouponViewModel
import kotlinx.android.synthetic.main.activity_add_coupon.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseStateResponseBody
import me.hgj.jetpackmvvm.ext.util.dp2px
import me.shaohui.bottomdialog.BottomDialog
import org.greenrobot.eventbus.EventBus
import java.util.*

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
        //领取渠道
        pickUpChannel.setOnClickListener {
            startActivityForResult(Intent(this, ChannelActivity::class.java).apply {
                putExtra(ChannelActivity.LITMIT_NUM, mViewModel.couponReceiveChannel.get())
            }, CHANNEL_NUM_SETTING)
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
                    mViewModel.startTime.get()
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

    companion object {
        /** 添加优惠券成功*/
        const val ADD_COUPON_SUCCESS = "ADD_COUPON_SUCCESS"

        /** 领取渠道*/
        const val CHANNEL_NUM_SETTING = 1000
    }


}