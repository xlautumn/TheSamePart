package com.same.part.assistant.helper

import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * 刷新结束的通知
 */
fun SmartRefreshLayout?.refreshComplete(hasMoreData: Boolean = true) {
    if (hasMoreData) {
        this?.finishRefresh()
        this?.finishLoadMore()
    } else {
        this?.finishRefreshWithNoMoreData()
        this?.finishLoadMoreWithNoMoreData()
    }
}