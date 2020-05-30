package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.CategoryData

class PurchaseSecondLevelAdapter(private val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = ArrayList<CategoryData>()
    private var mItemClickListener: ((Int, CategoryData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FirstLevelViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_purchase_second_level, null)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FirstLevelViewHolder) {
            data[position].let { categoryData ->
                holder.secondLevelName.text = categoryData.name
                holder.secondLevelName.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        if (categoryData.isSelected) R.color.color_0EB170 else R.color.color_9F9F9F
                    )
                )

                holder.itemView.setOnClickListener {
                    mItemClickListener?.invoke(position, categoryData)
                }
            }
        }
    }

    class FirstLevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var secondLevelName: TextView = itemView.findViewById(R.id.secondLevelName)

    }

    fun setOnItemClickListener(listener: (Int, CategoryData) -> Unit) {
        this.mItemClickListener = listener
    }

    fun setData(categoryData: ArrayList<CategoryData>?) {
        categoryData?.let {
            data.clear()
            data.addAll(categoryData)
            notifyDataSetChanged()
        }
    }
}