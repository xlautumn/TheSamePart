package com.same.part.assistant.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.gyf.immersionbar.ImmersionBar
import com.same.part.assistant.R
import com.same.part.assistant.activity.MainActivity
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.databinding.ActivityLoginBinding
import com.same.part.assistant.app.ext.showMessage
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.viewmodel.request.RequestLoginViewModel
import com.same.part.assistant.viewmodel.state.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState

/**
 * 登录页面
 */
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    private val mLoginViewModel: RequestLoginViewModel by lazy { getViewModel<RequestLoginViewModel>() }

    override fun layoutId(): Int = R.layout.activity_login

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).transparentNavigationBar().fullScreen(true).statusBarColor(R.color.color_08B070).init()
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
    }

    override fun createObserver() {
        mLoginViewModel.loginResult.observe(this, Observer {resultState ->
            parseState(resultState, {
                //登录成功
                CacheUtil.setShopUserModel(it)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, {
                //登录失败
                showMessage(it.errorMsg)
            })

        })
    }

    inner class ProxyClick {
        fun login() {
            when {
                mViewModel.phoneNum.value.isEmpty() -> showMessage("请填写账号")
                mViewModel.password.value.isEmpty() -> showMessage("请填写密码")
                else -> mLoginViewModel.loginReq(
                    mViewModel.phoneNum.value,
                    mViewModel.password.value
                )
            }
        }
    }

}