package com.same.part.assistant


import android.content.Context
import androidx.multidex.MultiDex


import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tencent.mmkv.MMKV
import me.hgj.jetpackmvvm.base.BaseApp
import kotlin.properties.Delegates


class TheSamePartApplication : BaseApp() {

    companion object {
        var context: Context by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        MultiDex.install(this)
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> ClassicsHeader(context) }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> ClassicsFooter(context).setDrawableSize(20F) }
    }
}