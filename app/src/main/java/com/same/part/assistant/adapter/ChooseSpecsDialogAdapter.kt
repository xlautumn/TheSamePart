package com.same.part.assistant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R

class ChooseSpecsDialogAdapter(private var mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedIndex = -1

    private var mChooseListener: OnChooseSpecsListener? = null

    private var mData: List<String>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            //一定注意 inflate的方法，在此处必须传viewGroup,若用2个参数，布局存在问题
            LayoutInflater.from(mContext)
                .inflate(R.layout.layout_dialog_choose_specs_list_item, parent, false)
        )

    override fun getItemCount(): Int = mData?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            mData?.let {
                holder.mText.text = it[position]

                holder.mText.setOnClickListener { text ->
                    //TODO 记录当前选中的值
                    selectedIndex = position
                    //用监听重置数据
                    mChooseListener?.apply {
                        mChooseListener?.onItemClick(position)
                    }
                }

                var isSelected = (position == selectedIndex)
                holder.mText.setTextColor(ContextCompat.getColor(mContext,
                    if (isSelected) R.color.color_0EB170 else R.color.color_666666))

                holder.mText.setBackgroundResource(
                    if (isSelected) R.drawable.shape_choose_spec_green_bg else R.drawable.shape_gray_rect_border)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mText: TextView = itemView.findViewById(R.id.tv_dialog_list)
    }

    fun setData(data: List<String>) {
        mData = data
        notifyDataSetChanged()
    }

    interface OnChooseSpecsListener {

        fun onItemClick(position: Int)

        fun addProductToCart()
    }

    class OnChooseSpecsListenerImpl : OnChooseSpecsListener {

        private lateinit var onItemClickListener: (Int) -> Unit
        private lateinit var addProductToCartListener: () -> Unit

        override fun onItemClick(position: Int) {
            this.onItemClickListener.invoke(position)
        }

        override fun addProductToCart() {
            this.addProductToCartListener.invoke()
        }

        fun onItemClick(listener: (Int) -> Unit) {
            this.onItemClickListener = listener
        }

        fun addProductToCart(listener: () -> Unit) {
            this.addProductToCartListener = listener
        }
    }

    fun setChooseSpecsDialogListener(listener: OnChooseSpecsListenerImpl.() -> Unit) {
        var back = OnChooseSpecsListenerImpl()
        back.listener()
        this.mChooseListener = back

    }
}