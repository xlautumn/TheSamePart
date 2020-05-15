package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.model.ProductClassificationModel
import kotlinx.android.synthetic.main.fragment_product_classification.*

/**
 * 收银商品
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
                "5113"
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

            }
        }
    }

    class ProductClassificationItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productId: TextView = itemView.findViewById(R.id.productId)
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productCount: TextView = itemView.findViewById(R.id.productCount)
        var productOperation: View = itemView.findViewById(R.id.productOperation)
    }

}