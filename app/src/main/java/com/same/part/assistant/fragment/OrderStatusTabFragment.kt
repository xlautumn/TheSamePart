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
import com.same.part.assistant.activity.*
import com.same.part.assistant.model.PurchaseOrderModel
import kotlinx.android.synthetic.main.fragment_order_status_tab.*

/**
 * 我的
 */
class OrderStatusTabFragment(var mContext: PurchaseOrderActivity, var title: String) : Fragment() {
    private val mOrderStatusList = arrayListOf<PurchaseOrderModel>().apply {
        add(
            PurchaseOrderModel(
                "546464646465",
                "￥1313",
                "2020-05-17 22:14:15",
                "0"
            )
        )
        add(
            PurchaseOrderModel(
                "5164133311",
                "￥13313",
                "2020-05-17 22:14:15",
                "1"
            )
        )
        add(
            PurchaseOrderModel(
                "515131331311",
                "￥133",
                "2020-05-17 22:14:15",
                "2"
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_status_tab, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mOrderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomAdapter(mOrderStatusList)
        }
    }

    inner class CustomAdapter(var dataList: ArrayList<PurchaseOrderModel>) :
        RecyclerView.Adapter<OrderStatusItemHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OrderStatusItemHolder =
            OrderStatusItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.purchase_order_status_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: OrderStatusItemHolder, position: Int) {
            val model = dataList[position]
            holder.orderId.text = model.id
            holder.orderPrice.text = model.price
            holder.orderTime.text = model.time
            holder.itemView.setOnClickListener {
                //跳转详情页
                Bundle().apply bundle@{
                    putInt(
                        OrderDetailActivity.STYLE_DETAIL_KEY,
                        OrderDetailActivity.STYLE_PURCHASE_DETAIL
                    )
                    mContext.startActivity(
                        Intent(mContext, OrderDetailActivity::class.java).apply {
                            putExtras(this@bundle)
                        }
                    )
                }
            }
        }
    }

    class OrderStatusItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderId: TextView = itemView.findViewById(R.id.orderId)
        var orderPrice: TextView = itemView.findViewById(R.id.orderPrice)
        var orderTime: TextView = itemView.findViewById(R.id.orderTime)
        var orderOperation: TextView = itemView.findViewById(R.id.orderOperation)
    }
}