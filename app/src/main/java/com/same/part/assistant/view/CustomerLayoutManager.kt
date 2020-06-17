package com.same.part.assistant.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager


class CustomerLayoutManager(private val maxRows: Int) : LayoutManager() {
    private var mMaxRow = 0
    private var mRow = 0

    init {
        isAutoMeasureEnabled = true
        setMaxRows(maxRows)
    }

    @Throws(Exception::class)
    fun setMaxRows(maxRows: Int) {
        if (maxRows < 0) {
            throw Exception("Rows need > 0")
        }
        mMaxRow = maxRows
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams =
        RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler)
            return
        }
        if (childCount == 0 && state.isPreLayout) {
            return
        }
        detachAndScrapAttachedViews(recycler)
        var topOffset = paddingTop
        var leftOffset = paddingLeft
        mRow = 1
        for (i in 0 until itemCount) {
            val child: View = recycler.getViewForPosition(i)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val itemWidth: Int = getDecoratedMeasurementHorizontal(child)
            val itemHeight: Int = getDecoratedMeasurementVertical(child)
            if (leftOffset + itemWidth <= getHorizontalSpace()) {
                layoutDecoratedWithMargins(
                    child,
                    leftOffset,
                    topOffset,
                    leftOffset + itemWidth,
                    topOffset + itemHeight
                )
            } else {
                if (mMaxRow != 0 && mMaxRow <= mRow) {
                    removeView(child)
                    break
                }
                leftOffset = paddingLeft
                topOffset += itemHeight
                layoutDecoratedWithMargins(
                    child,
                    leftOffset,
                    topOffset,
                    leftOffset + itemWidth,
                    topOffset + itemHeight
                )
                leftOffset += itemWidth
                mRow++
            }
        }
    }


    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin
    }

    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams
        return getDecoratedMeasuredWidth(view) + params.topMargin + params.bottomMargin
    }

    private fun getHorizontalSpace(): Int = width - paddingLeft

}