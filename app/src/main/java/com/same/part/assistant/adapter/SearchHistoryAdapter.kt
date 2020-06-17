package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R

class SearchHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        HistoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_search_history, parent, false)
        )

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HistoryViewHolder) {
            holder.name.text = mData[position]
        }
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.searchHistoryName)
    }

    fun setData(data: List<String>) {
        data.let {
            mData.clear()
            mData.addAll(it)
            notifyDataSetChanged()
        }
    }
}