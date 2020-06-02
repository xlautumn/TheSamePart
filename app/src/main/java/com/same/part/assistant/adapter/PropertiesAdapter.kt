package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.PropertyData

class PropertiesAdapter(private var data: MutableSet<PropertyData>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSelectView: View? = null
    private var mSelectProperty: PropertyData? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PropertiesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_dialog_choose_specs_list_properties_item, null)
        )

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PropertiesViewHolder) {
            data?.let {
                var property = it.elementAt(position)
                holder.property.text = property.name

                holder.property.setOnClickListener { view ->
                    mSelectView?.let { selectView ->
                        if (selectView != view && mSelectProperty
                            != property
                        ) {
                            mSelectProperty?.let { selectProperty ->
                                val newState = !selectProperty.isSelected
                                selectProperty.isSelected = newState

                                refreshViewState(selectView, selectProperty)
                            }
                        } else {
                            return@setOnClickListener
                        }

                    }
                    val newState = !property.isSelected
                    property.isSelected = newState
                    refreshViewState(view, property)

                    if (property.isSelected) {
                        mSelectView = view
                        mSelectProperty = property
                        //TODO 更新显示的文案

                    }
                }

                property.takeIf { pro -> pro.isSelected }?.let {
                    mSelectView = holder.property
                    mSelectProperty = property
                }

                holder.property.apply {
                    isSelected = property.isSelected
//                    setTextColor(
//                        ContextCompat.getColor(
//                            mContext,
//                            if (isSelected) R.color.color_0EB170 else R.color.color_666666
//                        )
//                    )
                    refreshDrawableState()
                }
            }
        }
    }

    private fun refreshViewState(
        selectView: View,
        property: PropertyData
    ) {
        selectView.isSelected = property.isSelected
        selectView.refreshDrawableState()
    }

    class PropertiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var property: TextView = view.findViewById(R.id.tv_dialog_list)
    }
}