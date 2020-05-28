package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R

class PurchaseSecondLevelAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mItemClickListener: ((Int, String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        FirstLevelViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_purchase_second_level, null)
        )

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FirstLevelViewHolder) {
            //设置一级分类得标题及图片
            holder.secondLevelName.text = "二级分类"
            //TODO 根据当前view是否选中设置颜色
            holder.secondLevelName.setTextColor(ContextCompat.getColor(mContext,R.color.color_9F9F9F))

            holder.itemView.setOnClickListener {
                mItemClickListener?.invoke(position, "")
            }
        }
    }

    class FirstLevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var secondLevelName: TextView = itemView.findViewById(R.id.secondLevelName)

    }

    fun setOnItemClickListener(listener: (Int, String) -> Unit) {
        this.mItemClickListener = listener
    }
}