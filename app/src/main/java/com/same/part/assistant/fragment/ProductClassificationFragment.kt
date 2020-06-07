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
import com.same.part.assistant.activity.AddProductClassificationActivity
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.ADDCLASSIFICATION_SUCCESS
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.CUSTOMCATEGORYID
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_ADD_SECOND_CATEGORY
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_EDIT
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.JUMP_FROM_TYPE
import com.same.part.assistant.activity.AddProductClassificationActivity.Companion.PARENT_ID
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.ProductClassificationModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.fragment_product_classification.*
import kotlinx.android.synthetic.main.fragment_product_classification.mSmartRefreshLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 商品分类
 */
class ProductClassificationFragment : Fragment() {
    //当前请求第几页的数据
    private var mCurrentPage: Int = 0

    private val mProductClassificationList = ArrayList<ProductClassificationModel>()


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRefresh(messageEvent: String) {
        if (ADDCLASSIFICATION_SUCCESS == messageEvent) {
            mSmartRefreshLayout.autoRefresh()
        }
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
        EventBus.getDefault().register(this)
        //列表数据
        productRecyclerView.apply {
            adapter = CustomAdapter(mProductClassificationList)
            layoutManager = LinearLayoutManager(context)
        }
        //定时刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mCurrentPage = 0
                loadProductClassificationList(page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadProductClassificationList(page = mCurrentPage, isRefresh = false)
            }
        }
        //加载数据
        loadProductClassificationList(page = mCurrentPage, isRefresh = true)
    }

    /**
     * 请求商品分类列表
     */
    private fun loadProductClassificationList(
        name: String = "",
        page: Int,
        size: String = "${Int.MAX_VALUE}",
        isRefresh: Boolean
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}custom-categories")
            .append("?page=$page")
            .append("&name=$name")
            .append("&size=$size")
            .append(
                "&shopId=${CacheUtil.getShopUserModel()?.UserShopDTO?.takeIf { it.isNotEmpty() }?.get(
                    0
                )?.shop?.shopId}"
            )
            .append("&appKey=${CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appKey}")
            .append("&appSecret=${CacheUtil.getShopUserModel()?.AccessToken?.easyapi?.appSecret}")
        HttpUtil.instance.getUrlWithHeader("WSCX", CacheUtil.getToken(), url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.apply {
                    getJSONArray("content")?.takeIf { it.size >= 0 }?.apply {
                        if (this.size == 0) {
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(false)
                            mCurrentPage--
                            //检查是否展示空布局
                            productRecyclerView.setEmptyView(emptyView)
                        } else {
                            val itemList = ArrayList<ProductClassificationModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    val id = getString("customCategoryId")
                                    val name = getString("name")
                                    val parentId =
                                        getJSONObject("parent")?.getString("customCategoryId")
                                            .orEmpty()
                                    ProductClassificationModel(id, name, parentId).apply {
                                        itemList.add(this)
                                    }
                                }
                            }
                            //如果是刷新需要清空之前的数据
                            if (isRefresh && itemList.size > 0) {
                                mProductClassificationList.clear()
                                mProductClassificationList.addAll(itemList)
                            } else {
                                mProductClassificationList.addAll(itemList)
                            }
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(true)
                            //刷新数据
                            productRecyclerView.adapter?.notifyDataSetChanged()
                            //检查是否展示空布局
                            productRecyclerView.setEmptyView(emptyView)
                        }

                    } ?: also {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        mCurrentPage--
                        //检查是否展示空布局
                        productRecyclerView.setEmptyView(emptyView)
                    }
                }
            } catch (e: Exception) {
                //请求失败回退请求的页数
                if (mCurrentPage > 0) mCurrentPage--
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(true)
                //检查是否展示空布局
                productRecyclerView.setEmptyView(emptyView)
            }
        }, {
            //请求失败回退请求的页数
            if (mCurrentPage > 0) mCurrentPage--
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
            //检查是否展示空布局
            productRecyclerView.setEmptyView(emptyView)
        })
    }

    inner class CustomAdapter(var dataList: ArrayList<ProductClassificationModel>) :
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
            holder.productName.text = model.name
            holder.productLevel.text = if (model.parentId.isNullOrEmpty()) {
                holder.addSecondCategory.visibility = View.VISIBLE
                "一级"
            } else {
                holder.addSecondCategory.visibility = View.GONE
                "二级"
            }

            //添加二级页面
            holder.addSecondCategory.setOnClickListener {
                startActivity(
                    Intent(context, AddProductClassificationActivity::class.java).apply {
                        putExtra(JUMP_FROM_TYPE, JUMP_FROM_ADD_SECOND_CATEGORY)
                        putExtra(PARENT_ID, model.id)
                    }
                )
            }
            //编辑
            holder.edit.setOnClickListener {
                startActivity(
                    Intent(context, AddProductClassificationActivity::class.java).apply {
                        putExtra(JUMP_FROM_TYPE, JUMP_FROM_EDIT)
                        putExtra(CUSTOMCATEGORYID, model.id)
                        if (model.parentId.isNotEmpty()) {
                            putExtra(PARENT_ID, model.parentId)
                        }
                    }
                )
            }
        }
    }

    class ProductClassificationItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productName: TextView = itemView.findViewById(R.id.productName)
        var productLevel: TextView = itemView.findViewById(R.id.productLevel)
        var addSecondCategory: View = itemView.findViewById(R.id.addSecondCategory)
        var edit: View = itemView.findViewById(R.id.edit)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}