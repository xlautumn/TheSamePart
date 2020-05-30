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
import com.same.part.assistant.data.model.CategoryData

class PurchaseFirstLevelAdapter(private var mContext: Context) :
    BaseAdapter() {

    private var data = ArrayList<CategoryData>()

    override fun getCount(): Int {
        return data.size
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
            viewHolder.leftName = view.findViewById(R.id.tv_category_name)
            viewHolder.rootFirstLevel = view.findViewById(R.id.rootFirstLevel)
            viewHolder.selectedBar = view.findViewById(R.id.selected_bar)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        data[position].let {
            viewHolder.leftName?.text = it.name

            viewHolder.rootFirstLevel?.setBackgroundColor(
                ContextCompat.getColor(
                    mContext,
                    if (it.isSelected) R.color.color_FFFFFF else R.color.color_F7F7F7
                )
            )
            viewHolder.selectedBar?.visibility = if (it.isSelected) View.VISIBLE else View.INVISIBLE
        }
        return view
    }

    fun setData(categoryData: ArrayList<CategoryData>?) {
        categoryData?.let {
            data.clear()
            data.addAll(categoryData)
            notifyDataSetChanged()
        }
    }

    class ViewHolder {
        var rootFirstLevel: ConstraintLayout? = null
        var leftName: TextView? = null
        var selectedBar: ImageView? = null
    }
}

