package com.same.part.assistant.viewmodel.state

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

class ChangePwdViewModel (application: Application) : BaseViewModel(application) {

    /** 新密码 */
    var newPwd = StringObservableField()

    /** 确认密码 */
    var confirmPwd = StringObservableField()
}