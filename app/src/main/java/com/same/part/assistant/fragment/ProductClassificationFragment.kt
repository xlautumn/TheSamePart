package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddProductClassificationActivity
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_ADD_SECOND_CATEGORY
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_EDIT
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_TYPE
import com.same.part.assistant.data.model.ProductClassificationModel
import kotlinx.android.synthetic.main.fragment_product_classification.*

/**
 * 商品分类
 */
class ProductClassificationFragment : Fragment() {
    private val mProductClassificationList = arrayListOf<ProductClassificationModel>().apply {
        add(
            ProductClassificationModel(
                "https38",
                "多多",
                "一级"
            )
        )
        add(
            ProductClassificationModel(
                "htt",
                "时间",
                "一级"
            )
        )
        add(
            ProductClassificationModel(
                "20131108",
                "果园",
                "二级"
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_classification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //列表数据
        productRecyclerView.apply {
            adapter = CustomAdapter(mProductClassificationList)
            layoutManager = LinearLayoutManager(context)
        }
    }

    inner class CustomAdapter(var dataList: ArrayList<ProductClassificationModel>) :
        RecyclerView.Adapter<ProductClassificationItemHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ProductClassificationItemHolder =
            ProductClassificationItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.product_classification_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: ProductClassificationItemHolder, position: Int) {
            val model = dataList[position]
            holder.productName.text = model.name
            holder.productLevel.text = model.level
            //添加二级页面
            holder.addSecondCategory.setOnClickListener {
                startActivity(
                    Intent(context, AddProductClassificationActivity::class.java).apply {
                        putExtra(JUMP_FROM_TYPE, JUMP_FROM_ADD_SECOND_CATEGORY)
                    }
                )
            }
            //编辑
            holder.edit.setOnClickListener {
                startActivity(
                    Intent(context, AddProductClassificationActivity::class.java).apply {
                        putExtra(JUMP_FROM_TYPE, JUMP_FROM_EDIT)
                    }
                )
            }
        }
    }

    class ProductClassificationItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productLevel: TextView = itemView.findViewById(R.id.productLevel)
        var addSecondCategory: View = itemView.findViewById(R.id.addSecondCategory)
        var edit: View = itemView.findViewById(R.id.edit)
    }
}