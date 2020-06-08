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
import com.alibaba.fastjson.JSONObject
import com.same.part.assistant.R
import com.same.part.assistant.activity.PurchaseOrderActivity
import com.same.part.assistant.activity.PurchaseOrderActivity.Companion.TITLES
import com.same.part.assistant.activity.PurchaseOrderDetailActivity
import com.same.part.assistant.activity.PurchaseOrderDetailActivity.Companion.ORDER_DETAIL_KEY
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierGoodItemModel
import com.same.part.assistant.data.model.PurchaseOrderModel
import com.same.part.assistant.helper.PayHelper
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.fragment_cashier.mSmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_order_status_tab.*

/**
 * 采购订单状态页面
 */
class OrderStatusTabFragment(var mContext: PurchaseOrderActivity, var title: String) : Fragment() {
    //采购订单列表
    private val mOrderStatusList = ArrayList<PurchaseOrderModel>()

    //当前请求第几页的数据
    private var mCurrentPage = 0

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

        //下拉刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mCurrentPage = 0
                loadPurchaseOrderList(state = getStatus(), page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadPurchaseOrderList(state = getStatus(), page = mCurrentPage, isRefresh = false)
            }
        }
        //请求数据
        loadPurchaseOrderList(state = getStatus(), page = mCurrentPage, isRefresh = true)
    }

    /**
     * 加载订单列表
     */
    private fun loadPurchaseOrderList(
        state: Int,
        page: Int = 0,
        size: String = "20",
        type: String = "1",
        isRefresh: Boolean
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}order/getProductOrderList")
            .append("?state=$state")
            .append("&page=$page")
            .append("&size=$size")
            .append("&userId=${CacheUtil.getUserId()}")
            .append("&type=$type")
            .append("&appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
        HttpUtil.instance.getUrl(url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.apply {
                    getJSONArray("content")?.takeIf { array -> array.size > 0 }?.apply {
                        if (this.size == 0) {
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(false)
                            mCurrentPage--
                            //检查是否展示空布局
                            mOrderRecyclerView.setEmptyView(emptyView)
                        } else {
                            val orderList = ArrayList<PurchaseOrderModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    val productOrderId = getString("productOrderId") ?: ""
                                    val no = getString("no") ?: "--"
                                    val addTime = getString("addTime") ?: "--"
                                    val totalPrice = getString("price") ?: "--"
                                    val status = getString("state") ?: "--"
                                    val payState = getString("payState") ?: "--"
                                    val province = getString("province").orEmpty()
                                    val city = getString("city").orEmpty()
                                    val district = getString("district").orEmpty()
                                    val address = getString("address").orEmpty()
                                    val addrName = getString("addrName") ?: "--"
                                    val addrMobile = getString("addrMobile") ?: "--"
                                    val payment = (getString("payment") ?: "--").ifEmpty { "--" }
                                    val nickname = getString("nickname") ?: "--"
                                    val nicktel = getString("nicktel") ?: "--"
                                    val nickDeliveryTime = getString("nickDeliveryTime") ?: "--"
                                    val nickServiceTime = getString("nickServiceTime") ?: "--"
                                    //订单详情条目的列表数据
                                    val orderItemList = ArrayList<CashierGoodItemModel>()
                                    getJSONArray("orderItems")?.takeIf { array -> array.size > 0 }
                                        ?.apply {
                                            for (index in 0 until this.size) {
                                                getJSONObject(index)?.apply {
                                                    val img = getString("img")
                                                    val name = getString("name")
                                                    val quantity = getString("quantity")
                                                    val price = getString("price")
                                                    CashierGoodItemModel(
                                                        img,
                                                        name,
                                                        quantity,
                                                        price
                                                    ).apply {
                                                        orderItemList.add(this)
                                                    }
                                                }
                                            }
                                        }
                                    PurchaseOrderModel(
                                        productOrderId,
                                        no,
                                        addTime,
                                        totalPrice,
                                        status,
                                        payState,
                                        province,
                                        city,
                                        district,
                                        address,
                                        addrName,
                                        addrMobile,
                                        payment,
                                        nickname,
                                        nicktel,
                                        nickDeliveryTime,
                                        nickServiceTime,
                                        orderItemList
                                    ).apply {
                                        orderList.add(this)
                                    }
                                }
                            }
                            //如果是刷新需要清空之前的数据
                            if (isRefresh && orderList.size > 0) {
                                mOrderStatusList.clear()
                                mOrderStatusList.addAll(orderList)
                            } else {
                                mOrderStatusList.addAll(orderList)
                            }
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(true)
                            //刷新数据
                            mOrderRecyclerView.adapter?.notifyDataSetChanged()
                            //检查是否展示空布局
                            mOrderRecyclerView.setEmptyView(emptyView)
                        }
                    } ?: also {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        mCurrentPage--
                        //检查是否展示空布局
                        mOrderRecyclerView.setEmptyView(emptyView)
                    }
                }
            } catch (e: Exception) {
                //请求失败回退请求的页数
                if (mCurrentPage > 0) mCurrentPage--
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
                //检查是否展示空布局
                mOrderRecyclerView.setEmptyView(emptyView)
            }
        }, {
            //请求失败回退请求的页数
            if (mCurrentPage > 0) mCurrentPage--
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
            //检查是否展示空布局
            mOrderRecyclerView.setEmptyView(emptyView)

        })

    }

    /**
     * 获取订单状态
     * 待付款0、待发货3、待收货2、已完成1、已取消-1
     */
    private fun getStatus(): Int = when (title) {
        TITLES[0] -> 0
        TITLES[1] -> 3
        TITLES[2] -> 2
        TITLES[3] -> -1
        TITLES[4] -> 1
        else -> 0
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
            holder.orderTime.text = "订单时间：${model.time}"
            holder.orderId.text = "订单编号：${model.no}"
            holder.orderPrice.text = "￥${model.price}"
            //待付款0、待发货3、待收货2、已完成1、已取消-1
            if (model.state in listOf("0", "2")) {
                holder.orderOperation.visibility = View.VISIBLE
                //0是待付款，1是已付款
                if (model.payState == "1") {
                    holder.orderOperation.text = "确认收货"
                } else {
                    holder.orderOperation.text = "去支付"
                }

            } else {
                holder.orderOperation.visibility = View.GONE
            }
            if (model.state in listOf("1", "-1")) {
                holder.orderStatus.setTextColor(0xFF999999.toInt())
            } else {
                holder.orderStatus.setTextColor(0xFFE76612.toInt())
            }

            if (model.payState == "0") {
                holder.orderOperation.setOnClickListener {
                    PayHelper(mContext).showPaymentChannel(model.productOrderId)
                }
            } else {
                holder.orderId.setOnClickListener(null)
            }
            holder.orderStatus.text = model.getStatements()
            holder.itemView.setOnClickListener {
                //跳转详情页
                startActivity(Intent(mContext, PurchaseOrderDetailActivity::class.java).apply {
                    putExtra(ORDER_DETAIL_KEY, model)
                })
            }
        }
    }

    class OrderStatusItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderId: TextView = itemView.findViewById(R.id.orderId)
        var orderPrice: TextView = itemView.findViewById(R.id.orderPrice)
        var orderTime: TextView = itemView.findViewById(R.id.orderTime)
        var orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        var orderOperation: TextView = itemView.findViewById(R.id.orderOperation)
    }
}

/**
 * state:待付款0、待发货3、待收货2、已完成1、已取消-1
 * payState:未付款：0；已付款1
 */
 fun PurchaseOrderModel.getStatements():String {
   return when (state) {
        "0" -> "等待买家付款"
        "1" -> "订单已完成"
        "2" -> {
            when (payState) {
                "0" -> "等待买家付款"
                "1" -> "等待买家收货"
                else -> "等待买家付款"
            }
        }
        "3" -> "等待平台发货"
        else -> "订单已取消"
    }
}
