package com.same.part.assistant.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.same.part.assistant.R
import kotlinx.android.synthetic.main.activity_channel.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.shaohui.bottomdialog.BottomDialog

/**
 * 领取渠道
 */
class ChannelActivity : AppCompatActivity() {

    /** 限领次数 */
    private var mLimitNum: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)
        //标题
        mToolbarTitle.text = "领取渠道"
        //返回按钮
        mTitleBack.setOnClickListener {
            handBackResult()
        }
        //选择限领次数
        llLitmitNum.setOnClickListener {
            val dialog = BottomDialog.create(supportFragmentManager)
            dialog.setViewListener {
                handleDialogView(it,dialog)
            }.setLayoutRes(R.layout.dialog_channel_num).setDimAmount(0.4F)
                .setCancelOutside(true).setTag("mChooseUsingThreshold").show()
        }
        mLimitNum = intent.getIntExtra(LITMIT_NUM,mLimitNum)
        tvNum.text = if (mLimitNum == 0) {
            "不限制"
        } else {
            "${mLimitNum}次"
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handBackResult()
        }
        return false
    }

    /**
     * dialog view
     */
    private fun handleDialogView(
        it: View,
        dialog: BottomDialog
    ) {
        val radioOneTime = it.findViewById<RadioButton>(R.id.radio_one_time)
        val radioSecondTime = it.findViewById<RadioButton>(R.id.radio_second_time)
        val radioThreeTime = it.findViewById<RadioButton>(R.id.radio_three_time)
        val radioNoLimitTime = it.findViewById<RadioButton>(R.id.radio_no_limit)
        when (mLimitNum) {
            1 -> {
                radioOneTime.isChecked = true
            }
            2 -> {
                radioSecondTime.isChecked = true
            }
            3 -> {
                radioThreeTime.isChecked = true
            }
            else -> {
                radioNoLimitTime.isChecked = true
            }
        }
        it.findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            when {
                radioOneTime.isChecked -> mLimitNum = 1
                radioSecondTime.isChecked -> mLimitNum = 2
                radioThreeTime.isChecked -> mLimitNum = 3
                radioNoLimitTime.isChecked -> mLimitNum = 0
            }
            tvNum.text = if (mLimitNum == 0) {
                "不限制"
            } else {
                "${mLimitNum}次"
            }
            dialog.dismissAllowingStateLoss()
        }
    }

    /**
     * 返回
     */
    private fun handBackResult() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(LITMIT_NUM, mLimitNum)
        })
        finish()
    }

    companion object {
        const val LITMIT_NUM = "LITMIT_NUM"
    }


}