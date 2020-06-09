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
import com.alibaba.fastjson.JSONObject
import com.same.part.assistant.R
import com.same.part.assistant.activity.CashierOrderDetailActivity.Companion.ORDER_DETAIL_KEY
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierGoodItemModel
import com.same.part.assistant.data.model.CashierOrderModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import com.same.part.assistant.utils.Util.Companion.format2
import kotlinx.android.synthetic.main.activity_cashier_order.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 收银订单
 */
class CashierOrderActivity : AppCompatActivity() {
    //收银订单列表
    private val mCashierOrderList = ArrayList<CashierOrderModel>()

    //当前请求第几页的数据
    private var mCurrentPage = 0

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
        //下拉刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mCurrentPage = 0
                loadCashierGoodList(page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadCashierGoodList(page = mCurrentPage, isRefresh = false)
            }
        }
        //请求收银商品列表
        loadCashierGoodList(page = mCurrentPage, isRefresh = true)
    }

    /**
     * 请求加载收银商品列表
     */
    private fun loadCashierGoodList(
        page: Int = 0,
        size: String = "20",
        isRefresh: Boolean
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}order/getProductOrderList")
            .append("?shopId=${CacheUtil.getShopId()}")
            .append("&page=$page")
            .append("&size=$size")
            .append("&type=2")
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
                            mCashierRecyclerView.setEmptyView(emptyView)
                        } else {
                            val orderList = ArrayList<CashierOrderModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    val no = getString("no") ?: "--"
                                    val price = getString("price") ?: "--"
                                    val payment =
                                        (getString("payment") ?: "现金支付").ifEmpty { "现金支付" }
                                    val addTime = getString("addTime") ?: "--"
                                    val shopCouponPrice = getString("shopCouponPrice") ?: "0.00"
                                    val platformCouponPrice =
                                        getString("platformCouponPrice") ?: "0.00"
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
                                                    //1--代表是收银商品 ,称重商品
                                                    //2--代表是收银商品，非称重商品
                                                    var type = "2"
                                                    var oldPrice = ""
                                                    var weight = ""
                                                    getJSONObject("product")?.apply {
                                                        type = getString("type")
                                                    }
                                                    //称重商品
                                                    if (type == "1") {
                                                        weight = getString("weight") + "kg"
                                                        oldPrice = format2(
                                                            "${price.toDouble() * getString("weight").toDouble()}"
                                                        ).orEmpty()
                                                    }
                                                    CashierGoodItemModel(
                                                        img,
                                                        name,
                                                        quantity,
                                                        price,
                                                        type,
                                                        weight,
                                                        oldPrice
                                                    ).apply {
                                                        orderItemList.add(this)
                                                    }
                                                }
                                            }
                                        }
                                    CashierOrderModel(
                                        no,
                                        price,
                                        payment,
                                        addTime,
                                        shopCouponPrice,
                                        platformCouponPrice,
                                        orderItemList
                                    ).apply {
                                        orderList.add(this)
                                    }
                                }
                            }
                            //如果是刷新需要清空之前的数据
                            if (isRefresh && orderList.size > 0) {
                                mCashierOrderList.clear()
                                mCashierOrderList.addAll(orderList)
                            } else {
                                mCashierOrderList.addAll(orderList)
                            }
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(true)
                            //刷新数据
                            mCashierRecyclerView.adapter?.notifyDataSetChanged()
                            //检查是否展示空布局
                            mCashierRecyclerView.setEmptyView(emptyView)
                        }
                    } ?: also {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        mCurrentPage--
                        //检查是否展示空布局
                        mCashierRecyclerView.setEmptyView(emptyView)
                    }
                }
            } catch (e: Exception) {
                //请求失败回退请求的页数
                if (mCurrentPage > 0) mCurrentPage--
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
                //检查是否展示空布局
                mCashierRecyclerView.setEmptyView(emptyView)
            }
        }, {
            //请求失败回退请求的页数
            if (mCurrentPage > 0) mCurrentPage--
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
            //检查是否展示空布局
            mCashierRecyclerView.setEmptyView(emptyView)
        })
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
            holder.orderId.text = model.no
            holder.orderAmount.text = "￥${model.price}"
            holder.payMethod.text = model.payment
            holder.time.text = model.addTime?.replace(" ", "\n")
            holder.itemView.setOnClickListener {
                //跳转详情页
                startActivity(
                    Intent(
                        this@CashierOrderActivity,
                        CashierOrderDetailActivity::class.java
                    ).apply {
                        putExtra(ORDER_DETAIL_KEY, model)
                    }
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