package com.same.part.assistant.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.data.model.ChangePwdInfo
import com.same.part.assistant.databinding.ActivityChangePasswordBinding
import com.same.part.assistant.viewmodel.request.RequsetChangePwdViewmodel
import com.same.part.assistant.viewmodel.state.ChangePwdViewModel
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 修改密码
 */
class ChangePasswordActivity : BaseActivity<ChangePwdViewModel, ActivityChangePasswordBinding>() {

    private val mRequsetChangePwdViewmodel: RequsetChangePwdViewmodel by lazy { getViewModel<RequsetChangePwdViewmodel>() }

    private lateinit var mUserId: String

    override fun layoutId(): Int = R.layout.activity_change_password

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        //标题
        mToolbarTitle.text = "修改密码"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        mUserId = intent?.getStringExtra(USER_ID_KEY).orEmpty()

        confirmChange.setOnClickListener {
            if (checkConditions()) {
                mRequsetChangePwdViewmodel.changeAccountPwd(
                    ChangePwdInfo(
                        mUserId,
                        mViewModel.newPwd.get()
                    )
                )
            }
        }
    }

    private fun checkConditions(): Boolean {
        if (mViewModel.newPwd.get().isEmpty()) {
            ToastUtils.showShort("请输入新密码")
            return false
        }

        if (mViewModel.confirmPwd.get().isEmpty()) {
            ToastUtils.showShort("请确认密码")
            return false
        }

        if (!isLetterDigit(mViewModel.newPwd.get()) || !isLetterDigit(mViewModel.confirmPwd.get())) {
            ToastUtils.showShort("请确认密码是否为字母+数字组合")
            return false
        }

        if (mViewModel.newPwd.get().length < 8 || mViewModel.confirmPwd.get().length < 8) {
            ToastUtils.showShort("请确认密码位数是否为8位及以上")
            return false
        }

        if (mViewModel.confirmPwd.get() != mViewModel.newPwd.get()) {
            ToastUtils.showShort("请确认密码需和新密码是否保持一致")
            return false
        }

        return true
    }

    /**
     * 字母+数字
     */
    private fun isLetterDigit(str: String): Boolean {
        var isDigit = false
        var isLetter = false
        for (i in str.indices) {
            if (Character.isDigit(str[i])) {
                isDigit = true
            }
            if (Character.isLetter(str[i])) {
                isLetter = true
            }
        }
        val regex = "^[a-zA-Z0-9]+$";
        return isDigit && isLetter && str.matches(regex.toRegex())
    }

    override fun createObserver() {
        mRequsetChangePwdViewmodel.changePwdResult.observe(this, Observer { resultState ->
            parseState(resultState, {
                //修改成功
                ToastUtils.showShort("密码修改成功")
                finish()
            }, {
                //登录失败
                showMessage(it.errorMsg)
            })
        })
    }

    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
    }

}