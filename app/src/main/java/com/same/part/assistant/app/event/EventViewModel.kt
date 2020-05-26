package me.hgj.jetpackmvvm.demo.app.event

import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.UnPeekLiveData

/**
 * 描述　:APP全局的Viewmodel，可以在这里发送全局通知替代Eventbus，LiveDataBus等
 */
class EventViewModel(app: Application) : BaseViewModel(app) {

}