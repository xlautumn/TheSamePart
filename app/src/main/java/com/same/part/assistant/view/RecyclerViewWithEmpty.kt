package com.same.part.assistant.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewWithEmpty : RecyclerView {
    private var emptyView: View? = null

    constructor(context: Context?) : this(context!!, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context!!, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    )

    //数据监听者
    private val adapterDataObserver: AdapterDataObserver =
        object : AdapterDataObserver() {
            override fun onChanged() {
                checkIfEmpty()
            }

            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                checkIfEmpty()
            }

            override fun onItemRangeRemoved(
                positionStart: Int,
                itemCount: Int
            ) {
                checkIfEmpty()
            }
        }

    private fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {
            val emptyViewVisible = adapter!!.itemCount == 0
            emptyView!!.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(adapterDataObserver)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(adapterDataObserver)
        checkIfEmpty()
    }

    //设置没有内容时，提示用户的空布局
    fun setEmptyView(emptyView: View?) {
        this.emptyView = emptyView
        checkIfEmpty()
    }

    companion object {
        private const val TAG = "EmptyRecyclerView"
    }
}