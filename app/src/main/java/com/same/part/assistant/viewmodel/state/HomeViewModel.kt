package com.same.part.assistant.viewmodel.state
import android.app.Application
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.callback.livedata.StringLiveData

class HomeViewModel (application: Application): BaseViewModel(application) {

    var imageUrl = StringObservableField()

    var name = StringObservableField()
}