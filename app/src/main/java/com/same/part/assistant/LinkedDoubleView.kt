package com.same.part.assistant

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.same.part.assistant.adapter.LinkedDoubleViewAdapter
import com.same.part.assistant.manager.GoodPurchaseDataManager

class LinkedDoubleView : LinearLayout {

    private var mAdapter: LinkedDoubleViewAdapter? = null
    private var mLeftView: ListView? = null
    private var mRightView: ListView? = null
    private var runnable: Runnable? = null

    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }


    private fun initView(context: Context?) {
        context?.let {
            LayoutInflater.from(context).inflate(R.layout.layout_linked_double_view, this)
            orientation = HORIZONTAL
            mLeftView = findViewById(R.id.lv_left_list)
            mRightView = findViewById(R.id.lv_right_list)
        }
    }

    internal fun setAdapter(adapter: LinkedDoubleViewAdapter) {
        mAdapter = adapter
        refreshView()
    }


    private fun refreshView() {
        mLeftView?.adapter = mAdapter?.getLeftViewAdapter()
        runnable = Runnable {
            mLeftView?.let {
                if (it.getChildAt(0) != null) {
                    it.setSelection(0)
                    val view = it.getChildAt(0)
                    view?.setBackgroundColor(ContextCompat.getColor(context, R.color.color_FFFFFF))
                    view?.findViewById<ImageView>(R.id.selected_bar)?.visibility= View.INVISIBLE
                } else {
                    postDelayed(runnable, 200)
                }
            }
        }
        postDelayed(runnable, 200)

        mLeftView?.setOnItemClickListener { parent, view, position, id ->
            //需要得效果，点击左边item 直接定位到当前分类得标题
            var count = 0
            val end = position.shl(1)
            //因为rightView中设置有子类别的标题控件，会占用一个子适配器
            //所以会有两倍leftView条目数得子适配器
            for (i in 0 until end) {
                count += mAdapter?.getMergeAdapter()?.getAdapter(count)?.count ?: 0
            }
            try {
                mRightView?.setSelection(count)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mRightView?.adapter =mAdapter?.getMergeAdapter()
        mRightView?.setOnScrollListener(object : AbsListView.OnScrollListener {
            var lastIndex = -1
            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                view?.let {
                    //此处获取的index有问题
                    var index = it.adapter.getItemViewType(firstVisibleItem)
                    if (lastIndex != index) {
                        mLeftView?.setSelection(index)
                        val currentView = mLeftView?.getChildAt(index)
                        currentView?.apply {
                            if (lastIndex != -1) {
                                mLeftView?.getChildAt(lastIndex)?.apply {
                                    //设置未选中得颜色
                                    setBackgroundColor(
                                        ContextCompat.getColor(
                                            context,
                                            R.color.color_EEEEEE
                                        )
                                    )
                                    findViewById<ImageView>(R.id.selected_bar)?.visibility=View.INVISIBLE
                                } ?: return@let
                            }

                            setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_FFFFFF
                                )
                            )
                            findViewById<ImageView>(R.id.selected_bar)?.visibility=View.VISIBLE
                            if (index != 0) {
                                val number =
                                    GoodPurchaseDataManager.getInstance().goodPurchaseModels[index].selectNum
                                //number大于0 给一级分类设置 数表
                            }
                        }
                        lastIndex = index
                    }
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }
        })

    }

    internal fun selectedRightView(position: Int, rightPosition: Int) {
        var count = 0
        val end = position.shl(1)
        for (i in 0 until end) {
            count +=mAdapter?.getMergeAdapter()?.getAdapter(count)?.count?:0
        }
        val selectPosition = count + rightPosition + 1
        mRightView?.postDelayed(Runnable {
            mRightView?.setSelection(selectPosition)
        }, 200)

    }

    internal fun getLeftView(): ListView? = mLeftView
}