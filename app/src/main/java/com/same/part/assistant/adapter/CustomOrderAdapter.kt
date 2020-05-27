package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.data.model.GoodItemModel

class CustomOrderAdapter(var dataList: ArrayList<GoodItemModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodItemHolder =
        GoodItemHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.order_good_item,
                parent,
                false
            )
        )


    override fun getItemCount(): Int = dataList.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GoodItemHolder) {
            val model = dataList[position]
            Glide.with(holder.itemView.context).load(model.avatar).into(holder.goodAvatar)
            holder.goodName.text = model.name
            holder.goodNum.text = "x${model.number}"
            holder.goodPrice.text = model.price
        }
    }

    class GoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPrice: TextView = itemView.findViewById(R.id.goodPrice)
    }
}