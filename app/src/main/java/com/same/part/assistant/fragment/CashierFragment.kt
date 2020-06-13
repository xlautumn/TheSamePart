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
import com.same.part.assistant.activity.AddCashierGoodActivity
import com.same.part.assistant.activity.AddCashierGoodActivity.Companion.ADD_OR_UPDATE_CASHIER_SUCCESS
import com.same.part.assistant.activity.AddCashierGoodActivity.Companion.CASHIER_PRODUCT_ID
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CashierModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.fragment_cashier.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 收银商品
 */
class CashierFragment : Fragment() {
    //数据列表
    private val mCashierList = arrayListOf<CashierModel>()

    //当前请求第几页的数据
    private var mCurrentPage = 0

    //是否是搜索模式
    private var mIsSearchMode = false

    //当前搜索的关键字
    private var mCurrentSearchKey = ""

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRefresh(messageEvent: String) {
        if (ADD_OR_UPDATE_CASHIER_SUCCESS == messageEvent) {
            mSmartRefreshLayout.autoRefresh()
        }
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
        EventBus.getDefault().register(this)
        //列表数据
        cashierRecyclerView.apply {
            adapter = CustomAdapter(mCashierList)
            layoutManager = LinearLayoutManager(context)
        }
        //下拉刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                if (mIsSearchMode) {
                    searchData(mCurrentSearchKey, true)
                } else {
                    mCurrentPage = 0
                    loadCashierList(page = mCurrentPage, isRefresh = true)
                }
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                if (mIsSearchMode) {
                    searchData(mCurrentSearchKey, false)
                } else {
                    loadCashierList(page = mCurrentPage, isRefresh = false)
                }
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
        size: String = "20",
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
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.apply {
                    getJSONObject("msg")?.getJSONArray("data")?.takeIf { it.size >= 0 }?.apply {
                        if (this.size == 0) {
                            if (mIsSearchMode) {
                                //清空数据，展示没数据
                                mCashierList.clear()
                                //刷新数据
                                cashierRecyclerView.adapter?.notifyDataSetChanged()
                                //检查是否展示空布局
                                cashierRecyclerView.setEmptyView(emptyView)
                            } else {
                                //通知刷新结束
                                mSmartRefreshLayout?.refreshComplete(false)
                                mCurrentPage--
                                //检查是否展示空布局
                                cashierRecyclerView.setEmptyView(emptyView)
                            }
                        } else {
                            val itemList = ArrayList<CashierModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    val id = getString("productId")
                                    val name = getString("name")
                                    val price = getString("price")
                                    val unit = getString("unit")
                                    val type = getString("type")
                                    val state = getString("state")
                                    val quantity = getString("quantity")
                                    CashierModel(
                                        id,
                                        name,
                                        price,
                                        unit,
                                        state,
                                        quantity = quantity,
                                        type = type
                                    ).apply {
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
                            //检查是否展示空布局
                            cashierRecyclerView.setEmptyView(emptyView)
                        }

                    } ?: also {
                        if (mIsSearchMode) {
                            //清空数据，展示没数据
                            mCashierList.clear()
                            //刷新数据
                            cashierRecyclerView.adapter?.notifyDataSetChanged()
                            //检查是否展示空布局
                            cashierRecyclerView.setEmptyView(emptyView)
                        } else {
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(false)
                            mCurrentPage--
                            //检查是否展示空布局
                            cashierRecyclerView.setEmptyView(emptyView)
                        }
                    }
                }
            } catch (e: Exception) {
                //请求失败回退请求的页数
                if (mCurrentPage > 0) mCurrentPage--
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
                //检查是否展示空布局
                cashierRecyclerView.setEmptyView(emptyView)
            }
        }, {
            //请求失败回退请求的页数
            if (mCurrentPage > 0) mCurrentPage--
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
            //检查是否展示空布局
            cashierRecyclerView.setEmptyView(emptyView)
        })
    }

    inner class CustomAdapter(var dataList: ArrayList<CashierModel>) :
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
            holder.cashierPrice.text = "￥${model.price}"
            var cashQuantity = model.quantity
            if (model.type != "1" && cashQuantity.isNotEmpty() && cashQuantity.contains(".")) {
                val index = cashQuantity.indexOf(".")
                cashQuantity = cashQuantity.substring(0, index)
            }

            holder.cashierUnit.text = "${cashQuantity}${model.unit}"
            holder.shelves.text = if (model.status == "1") "上架" else "下架"
            holder.edit.apply {
                setOnClickListener {
                    startActivity(
                        Intent(context, AddCashierGoodActivity::class.java).apply {
                            putExtra(
                                AddCashierGoodActivity.JUMP_FROM_TYPE,
                                AddCashierGoodActivity.JUMP_FROM_EDIT
                            )
                            putExtra(CASHIER_PRODUCT_ID, model.id)
                        }
                    )
                }
            }
        }

    }

    class CashierItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cashierName: TextView = itemView.findViewById(R.id.cashierName)
        var cashierPrice: TextView = itemView.findViewById(R.id.cashierPrice)
        var cashierUnit: TextView = itemView.findViewById(R.id.cashierUnit)
        var edit: TextView = itemView.findViewById(R.id.edit)
        var shelves: TextView = itemView.findViewById(R.id.shelves)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 搜索
     */
    fun searchData(text: String, isRefresh: Boolean) {
        mCurrentSearchKey = text
        mIsSearchMode = true
        //从外面点击搜索的时候第一次会走这里
        if (isRefresh) {
            mCurrentPage = 0
        }
        loadCashierList(text, mCurrentPage, isRefresh = isRefresh)
    }

    /**
     * 取消搜索
     */
    fun cancelSearch() {
        if (mIsSearchMode) {
            mCurrentSearchKey = ""
            mIsSearchMode = false
            mCurrentPage = 0
            loadCashierList(page = mCurrentPage, isRefresh = true)
            mSmartRefreshLayout?.setNoMoreData(false)
        }
    }
}