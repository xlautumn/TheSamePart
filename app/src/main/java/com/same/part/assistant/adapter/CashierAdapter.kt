package com.same.part.assistant.adapter

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.app.ext.appendImageScaleSuffix
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.data.model.CustomCategory
import com.same.part.assistant.fragment.CashierFragmentV2

/**
 * 收银商品一级分类
 */
class CashierFirstLevelAdapter(private val proxy: CashierFragmentV2.Proxy) :
    BaseQuickAdapter<CustomCategory, BaseViewHolder>(R.layout.layout_item_cashier_first_level) {
    override fun convert(holder: BaseViewHolder, item: CustomCategory) {
        val isSelected = item.customCategoryId == proxy.getCurrentFirstCategoryId()
        holder.getView<View>(R.id.rootFirstLevel).setBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.color_FFFFFF else R.color.color_F7F7F7
            )
        )
        holder.setText(R.id.tv_category_name, item.name)
        holder.getView<View>(R.id.selected_bar)?.visibility =
            if (isSelected) View.VISIBLE else View.INVISIBLE
    }
}

/**
 * 收银商品二级分类
 */
class CashierSecondLevelAdapter(private val proxy: CashierFragmentV2.Proxy) :
    BaseQuickAdapter<CustomCategory, BaseViewHolder>(R.layout.layout_item_cashier_second_level) {
    override fun convert(holder: BaseViewHolder, item: CustomCategory) {
        val isSelected = item.customCategoryId == proxy.getCurrentSecondCategoryId()
        holder.setTextColor(
            R.id.secondLevelName,
            ContextCompat.getColor(
                holder.itemView.context,
                if (isSelected) R.color.color_0EB170 else R.color.color_9F9F9F
            )
        )
        holder.setText(R.id.secondLevelName, item.name)
    }
}

/**
 * 收银商品
 */
class CashierProductAdapter() :
    BaseQuickAdapter<CashierProduct, BaseViewHolder>(R.layout.layout_item_cashier_product) {
    override fun convert(holder: BaseViewHolder, item: CashierProduct) {
        if (item.img.isNullOrEmpty()) {
            holder.getView<ImageView>(R.id.iv_product_img).setImageResource(R.drawable.icn_head_img)
        } else {
            holder.getView<ImageView>(R.id.iv_product_img).let {
                Glide.with(it.context)
                    .load(it.appendImageScaleSuffix(item.img!!))
                    .into(it)
            }
        }
        var cashQuantity = item.quantity
        if (item.type != "1" && cashQuantity.isNotEmpty() && cashQuantity.contains(".")) {
            val index = cashQuantity.indexOf(".")
            cashQuantity = cashQuantity.substring(0, index)
        }
        holder.setText(R.id.tv_name, item.name)
        holder.setText(R.id.tv_price, "￥${item.price}")
        holder.setText(R.id.tv_quantity, "库存${cashQuantity}${item.unit}")
        if (item.state == "1") {
            holder.setTextColorRes(R.id.tv_operation, R.color.color_E15C00)
            holder.setText(R.id.tv_operation, "上架")
        } else {
            holder.setTextColorRes(R.id.tv_operation, R.color.color_0EB170)
            holder.setText(R.id.tv_operation, "下架")
        }

    }
}

