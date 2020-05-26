package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.data.model.PayWayModel

class PayWayDialogAdapter(var mContext: Context, private var mData: ArrayList<PayWayModel>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: ((PayWayModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PayWayViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.dialog_item_pay_way, parent, false)
        )

    override fun getItemCount(): Int = mData?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is PayWayViewHolder) {
            mData?.let { datas ->
                val payWayModel = datas[position]
                holder.payWayName.apply {
                    text = payWayModel.name
                    setTextColor(
                        ContextCompat.getColor(
                            mContext,
                            if (payWayModel.isChecked) R.color.color_0EB170 else R.color.color_000000
                        )
                    )
                }
                holder.imageView.visibility =
                    if (payWayModel.isChecked) View.VISIBLE else View.INVISIBLE

                holder.itemView.setOnClickListener {
                    updateState(datas, position, holder = holder)
                    mListener?.invoke(payWayModel)
                }
            }
        }

    }

    private fun updateState(
        data: ArrayList<PayWayModel>,
        position: Int,
        holder: PayWayViewHolder
    ) {
        //需要将原来选中得View状态清除，新选中得View状态更新
        for (index in data.indices) {
            val payWayModel = data[index]
            payWayModel.isChecked = (position == index)

            holder.payWayName.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    if (payWayModel.isChecked) R.color.color_0EB170 else R.color.color_000000
                )
            )
            holder.imageView.visibility =
                if (payWayModel.isChecked) View.VISIBLE else View.INVISIBLE
        }
    }

    class PayWayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.ivChecked)
        var payWayName: TextView = itemView.findViewById(R.id.payWayName)
    }

    fun setOnItemClickListener(listener: (PayWayModel)->Unit) {
        mListener = listener
    }
}

