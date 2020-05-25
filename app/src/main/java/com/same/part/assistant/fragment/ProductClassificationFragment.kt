package com.same.part.assistant.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhouwei.library.CustomPopWindow
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddProductActivity
import com.same.part.assistant.activity.AddProductClassificationActivity
import com.same.part.assistant.activity.ViewProductActivity
import com.same.part.assistant.model.ProductClassificationModel
import kotlinx.android.synthetic.main.fragment_product_classification.*
import kotlinx.android.synthetic.main.pop_product_operation.view.*

/**
 * 商品分类
 */
class ProductClassificationFragment : Fragment() {
    private val mProductClassificationList = arrayListOf<ProductClassificationModel>().apply {
        add(
            ProductClassificationModel(
                "https38",
                "多多",
                "511313"
            )
        )
        add(
            ProductClassificationModel(
                "htt",
                "时间",
                "21"
            )
        )
        add(
            ProductClassificationModel(
                "20131108",
                "果园",
                "0"
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

    class CustomAdapter(var dataList: ArrayList<ProductClassificationModel>) :
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
            holder.productId.text = model.id
            holder.productName.text = model.name
            holder.productCount.text = model.count
            holder.productOperation.setOnClickListener {
                showOperationPop(
                    holder.itemView.context,
                    holder.productOperationPic,
                    (model.count.toIntOrNull() ?: 0) > 0
                )
            }
        }

        /**
         * 操作弹框
         */
        private fun showOperationPop(context: Context, anchorView: View, hasProduct: Boolean) {
            LayoutInflater.from(context).inflate(R.layout.pop_product_operation, null).apply {
                optionTwo.text = if (hasProduct) "添加商品" else "添加子分类"
                optionThree.text = if (hasProduct) "查看商品" else "添加商品"
                val popWindow =
                    CustomPopWindow.PopupWindowBuilder(context).setView(this).create()
                        .showAsDropDown(anchorView)
                //第一个选项
                findViewById<View>(R.id.optionOne).setOnClickListener {
                    popWindow.dissmiss()
                    context.startActivity(
                        Intent(
                            context,
                            AddProductClassificationActivity::class.java
                        )
                    )
                }
                //第二个选项
                findViewById<View>(R.id.optionTwo).setOnClickListener {
                    popWindow.dissmiss()
                    context.startActivity(
                        Intent(
                            context,
                            if (hasProduct) AddProductActivity::class.java else AddProductClassificationActivity::class.java
                        )
                    )
                }
                //第三个选项
                findViewById<View>(R.id.optionThree).setOnClickListener {
                    popWindow.dissmiss()
                    context.startActivity(
                        Intent(
                            context,
                            if (hasProduct) ViewProductActivity::class.java else AddProductActivity::class.java
                        )
                    )
                }
            }
        }
    }

    class ProductClassificationItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productId: TextView = itemView.findViewById(R.id.productId)
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productCount: TextView = itemView.findViewById(R.id.productCount)
        var productOperation: View = itemView.findViewById(R.id.productOperation)
        var productOperationPic: View = itemView.findViewById(R.id.productOperationPic)
    }
}