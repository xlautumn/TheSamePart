package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.same.part.assistant.R

class PurchaseFirstLevelAdapter(private var mContext: Context) :
    BaseAdapter() {
    override fun getCount(): Int {
        return 11
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view: View
        var viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_purchase_first_level, null)
            viewHolder = ViewHolder()
            viewHolder.leftName = view.findViewById(R.id.tv_left_name)
            viewHolder.selectedBar = view.findViewById(R.id.selected_bar)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.leftName?.text = "一级标题"

        //todo 根据是否选中来设置背景色及 bar是否显示
        viewHolder.rootSecondLevel?.setBackgroundColor(
            ContextCompat.getColor(
                mContext,
                R.color.color_EEEEEE
            )
        )
        //选中
        viewHolder.selectedBar?.visibility=View.INVISIBLE

        return view
    }

    class ViewHolder {
        var rootSecondLevel: ConstraintLayout? = null
        var leftName: TextView? = null
        var selectedBar: ImageView? = null
    }
}

