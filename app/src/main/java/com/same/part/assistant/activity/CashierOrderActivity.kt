package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.model.CashierOrderModel
import kotlinx.android.synthetic.main.activity_cashier_order.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 收银订单
 */
class CashierOrderActivity : AppCompatActivity() {
    private val mCashierOrderList = arrayListOf<CashierOrderModel>().apply {
        add(
            CashierOrderModel(
                "220131108",
                "￥20.05",
                "支付宝",
                "2020-12-23\n11:10:24"
            )
        )
        add(
            CashierOrderModel(
                "220131108",
                "￥20.05",
                "微信",
                "2020-12-23\n11:20:24"
            )
        )
        add(
            CashierOrderModel(
                "220131108",
                "￥20.05",
                "现金支付",
                "2020-12-23\n11:22:04"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier_order)
        //标题
        mToolbarTitle.text = "收银订单"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mCashierRecyclerView.apply {
            adapter = CustomAdapter(mCashierOrderList)
            layoutManager = LinearLayoutManager(context)
        }
    }


    inner class CustomAdapter(var dataList: ArrayList<CashierOrderModel>) :
        RecyclerView.Adapter<CashierOrderItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashierOrderItemHolder =
            CashierOrderItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cashier_order_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: CashierOrderItemHolder, position: Int) {
            val model = dataList[position]
            holder.orderId.text = model.orderId
            holder.orderAmount.text = model.amount
            holder.payMethod.text = model.payMethod
            holder.time.text = model.time
            holder.itemView.setOnClickListener {
                //跳转详情页
                startActivity(
                    Intent(
                        this@CashierOrderActivity,
                        CashierOrderDetailActivity::class.java
                    )
                )
            }
        }

    }

    class CashierOrderItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderId: TextView = itemView.findViewById(R.id.orderId)
        var orderAmount: TextView = itemView.findViewById(R.id.orderAmount)
        var payMethod: TextView = itemView.findViewById(R.id.payMethod)
        var time: TextView = itemView.findViewById(R.id.time)
    }

}