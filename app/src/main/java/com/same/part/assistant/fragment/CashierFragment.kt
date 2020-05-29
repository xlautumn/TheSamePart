package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierModel
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.fragment_cashier.*

/**
 * 收银商品
 */
class CashierFragment : Fragment() {
    private val mCashierList = arrayListOf<CashierModel>().apply {
        add(
            CashierModel(
                "https38",
                "西蓝花",
                "￥5.00",
                "500g",
                false
            )
        )
        add(
            CashierModel(
                "https38",
                "西蓝花",
                "￥5.00",
                "500g",
                false
            )
        )
        add(
            CashierModel(
                "https38",
                "西蓝花",
                "￥5.00",
                "500g",
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
        //定时刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {

            }
            //上拉加载
            setOnLoadMoreListener {

            }
        }
        //加载数据
        loadCashierList()
    }

    /**
     * 请求收银商品列表
     * type中的1代表的是收银称重商品，2代表收银非称重商品
     */
    private fun loadCashierList(
        name: String = "",
        page: String = "0",
        size: String = "10",
        type: String = "1,2"
    ) {
        val url = "${ApiService.SERVER_URL}amountCommodity/get"
        val json = JSON.toJSONString(
            hashMapOf<String, String>(
                "WSCX" to CacheUtil.getToken(),
                "name" to name,
                "size" to size,
                "type" to type
            )
        )
        HttpUtil.instance.postUrl(url, json, {
            ToastUtils.showShort("请求成功")
        })
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
            holder.cashierName.text = model.name
            holder.cashierPrice.text = model.price
            holder.cashierUnit.text = model.unit
            holder.cashierOperation.apply {
                setOnClickListener {
                    model.status = !model.status
                    text = if (model.status) "启动" else "禁用"
                }
            }
        }

    }

    class CashierItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cashierName: TextView = itemView.findViewById(R.id.cashierName)
        var cashierPrice: TextView = itemView.findViewById(R.id.cashierPrice)
        var cashierUnit: TextView = itemView.findViewById(R.id.cashierUnit)
        var cashierOperation: TextView = itemView.findViewById(R.id.cashierOperation)
    }

}