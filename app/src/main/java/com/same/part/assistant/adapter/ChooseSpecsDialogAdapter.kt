package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.PropertyData

class ChooseSpecsDialogAdapter(
    private var mContext: Context,
    private var mData: MutableSet<PropertyData>
) :
    BaseAdapter() {
    class ViewHolder() {
        lateinit var propertiesView: RecyclerView
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View
        var holder: ViewHolder
        if (convertView == null || convertView.tag == null
            || convertView.tag !is ViewHolder
        ) {
            view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_dialog_choose_specs_list_item, parent, false)
            holder = ViewHolder()
            holder.apply {
                holder.propertiesView = view.findViewById(R.id.propertiesView)
                holder.propertiesView.apply {
                    layoutManager = GridLayoutManager(mContext,3)
                    adapter = PropertiesAdapter(mData)
                }
            }
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }
        //todo 设置
        return view
    }

    override fun getItem(position: Int): Any = mData

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = 1
}