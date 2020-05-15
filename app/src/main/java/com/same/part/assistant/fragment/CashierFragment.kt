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
import com.same.part.assistant.model.CashierModel
import kotlinx.android.synthetic.main.fragment_cashier.*

/**
 * 收银商品
 */
class CashierFragment : Fragment() {
    private val mCashierList = arrayListOf<CashierModel>().apply {
        add(
            CashierModel(
                "https38",
                "多多",
                "511313",
                "金卡",
                true
            )
        )
        add(
            CashierModel(
                "htt",
                "时间",
                "21",
                "银卡",
                false
            )
        )
        add(
            CashierModel(
                "20131108",
                "果园",
                "5113",
                "金卡",
                false
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cashier, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //列表数据
        cashierRecyclerView.apply {
            adapter = CustomAdapter(mCashierList)
            layoutManager = LinearLayoutManager(context)
        }
    }

    class CustomAdapter(var dataList: ArrayList<CashierModel>) :
        RecyclerView.Adapter<CashierItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashierItemHolder =
            CashierItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cashier_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: CashierItemHolder, position: Int) {
            val model = dataList[position]
            holder.cashierId.text = model.id
            holder.cashierName.text = model.name
            holder.cashierPrice.text = model.price
            holder.cashierSpecification.text = model.specification
            holder.cashierOperation.apply {
                setOnClickListener {
                    model.operation = !model.operation
                    text = if (model.operation) "启动" else "禁用"
                }
            }
        }

    }

    class CashierItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cashierId: TextView = itemView.findViewById(R.id.cashierId)
        var cashierName: TextView = itemView.findViewById(R.id.cashierName)
        var cashierPrice: TextView = itemView.findViewById(R.id.cashierPrice)
        var cashierSpecification: TextView = itemView.findViewById(R.id.cashierSpecification)
        var cashierOperation: TextView = itemView.findViewById(R.id.cashierOperation)
    }

}