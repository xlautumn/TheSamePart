package com.same.part.assistant.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhouwei.library.CustomPopWindow
import com.same.part.assistant.R
import com.same.part.assistant.data.model.ProductModel
import kotlinx.android.synthetic.main.activity_view_product.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 会员管理
 */
class ViewProductActivity : AppCompatActivity() {
    private val mProductList = arrayListOf<ProductModel>().apply {
        add(
            ProductModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "多多",
                "511313",
                "1000"
            )
        )
        add(
            ProductModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "时间",
                "21",
                "2000"

            )
        )
        add(
            ProductModel(
                "https://p5.gexing.com/GSF/touxiang/20200514/15/6ytxkxypkc25dybo9gy21c8s7.jpg@!200x200_3?recache=20131108",
                "果园",
                "5113",
                "30000"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)
        //标题
        mToolbarTitle.text = "查看商品"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        productRecyclerView.apply {
            adapter = CustomAdapter(mProductList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    class CustomAdapter(var dataList: ArrayList<ProductModel>) :
        RecyclerView.Adapter<ProductItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductItemHolder =
            ProductItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.product_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: ProductItemHolder, position: Int) {
            val model = dataList[position]
            holder.productId.text = model.id
            holder.productName.text = model.name
            holder.productPrice.text = model.price
            holder.productRepertory.text = model.repertory
            holder.productOperation.setOnClickListener {
                showOperationPop(holder.itemView.context, holder.productOperationPic, position)
            }
        }

        /**
         * 操作弹框
         */
        private fun showOperationPop(context: Context, anchorView: View, position: Int) {
            LayoutInflater.from(context).inflate(R.layout.pop_view_product_operation, null).apply {
                val popWindow =
                    CustomPopWindow.PopupWindowBuilder(context).setView(this).create()
                        .showAsDropDown(anchorView)
                //第一个选项
                findViewById<View>(R.id.optionOne).setOnClickListener {
                    popWindow.dissmiss()

                }
                //第二个选项
                findViewById<View>(R.id.optionTwo).setOnClickListener {
                    popWindow.dissmiss()
                    dataList.removeAt(position)
                    notifyDataSetChanged()
                }
            }
        }

    }

    class ProductItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productId: TextView = itemView.findViewById(R.id.productId)
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productPrice: TextView = itemView.findViewById(R.id.productPrice)
        var productRepertory: TextView = itemView.findViewById(R.id.productRepertory)
        var productOperation: View = itemView.findViewById(R.id.productOperation)
        var productOperationPic: View = itemView.findViewById(R.id.productOperationPic)
    }

}