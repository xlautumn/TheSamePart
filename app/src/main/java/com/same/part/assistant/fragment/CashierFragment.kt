package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.same.part.assistant.R
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.fragment_cashier.*

/**
 * 收银商品
 */
class CashierFragment : Fragment() {
    //数据列表
    private val mCashierList = arrayListOf<CashierModel>()
    //当前请求第几页的数据
    private var mCurrentPage = 0

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
                mCurrentPage = 0
                loadCashierList(page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadCashierList(page = mCurrentPage, isRefresh = false)
            }
        }
        //加载数据
        loadCashierList(page = mCurrentPage, isRefresh = true)
    }

    /**
     * 请求收银商品列表
     * type中的1代表的是收银称重商品，2代表收银非称重商品
     */
    private fun loadCashierList(
        name: String = "",
        page: Int = 0,
        size: String = "10",
        type: String = "1,2",
        isRefresh: Boolean
    ) {
        val url = "${ApiService.SERVER_URL}amountCommodity/get"
        val jsonMap = hashMapOf(
            "page" to "$page",
            "name" to name,
            "size" to size,
            "type" to type
        )
        HttpUtil.instance.postUrlWithHeader("WSCX", CacheUtil.getToken(), url, jsonMap, {
            val resultJson = JSONObject.parseObject(it)
            resultJson.apply {
                getJSONObject("msg")?.getJSONArray("data")?.takeIf { it.size >= 0 }?.apply {
                    if (this.size == 0) {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        mCurrentPage--
                    } else {
                        val itemList = ArrayList<CashierModel>()
                        for (i in 0 until this.size) {
                            getJSONObject(i)?.apply {
                                val id = getString("productId")
                                val name = getString("name")
                                val price = getString("price")
                                val unit = getString("unit")
                                val state = getString("state") == "1"
                                CashierModel(id, name, price, unit, state).apply {
                                    itemList.add(this)
                                }
                            }
                        }
                        //如果是刷新需要清空之前的数据
                        if (isRefresh && itemList.size > 0) {
                            mCashierList.clear()
                            mCashierList.addAll(itemList)
                        } else {
                            mCashierList.addAll(itemList)
                        }
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(true)
                        //刷新数据
                        cashierRecyclerView.adapter?.notifyDataSetChanged()
                    }

                }
            }
        }, {
            //请求失败回退请求的页数
            mCurrentPage--
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
                    text = if (model.status) "禁用" else "启动"
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