package com.same.part.assistant.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CustomInfoModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.activity_custom_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 客户管理
 */
class CustomManagerActivity : AppCompatActivity() {
    private val mCustomList = ArrayList<CustomInfoModel>()
    //当前请求第几页的数据
    private var mCurrentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_manager)
        //标题
        mToolbarTitle.text = "客户管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mManagerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomAdapter(mCustomList)
        }
        //下拉刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mCurrentPage = 0
                loadCustomManagerList(page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadCustomManagerList(page = mCurrentPage, isRefresh = false)
            }
        }
        //加载客户管理列表
        loadCustomManagerList(page = mCurrentPage, isRefresh = true)
    }

    private fun loadCustomManagerList(
        page: Int = 0,
        size: String = "20",
        isRefresh: Boolean
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}biz/customers")
            .append("?shopId=${CacheUtil.getShopId()}")
            .append("&appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
            .append("&page=$page")
            .append("&size=$size")
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
                            mManagerRecyclerView.setEmptyView(emptyView)
                        } else {
                            val listItems = ArrayList<CustomInfoModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.getJSONObject("user")?.apply {
                                    val photo = getString("photo")
                                    val nickName = getString("nickname")
                                    val mobile = getString("mobile")
                                    CustomInfoModel(photo, nickName, mobile).apply {
                                        listItems.add(this)
                                    }
                                }
                            }
                            //如果是刷新需要清空之前的数据
                            if (isRefresh && listItems.size > 0) {
                                mCustomList.clear()
                                mCustomList.addAll(listItems)
                            } else {
                                mCustomList.addAll(listItems)
                            }
                            //通知刷新结束
                            mSmartRefreshLayout?.refreshComplete(true)
                            //刷新数据
                            mManagerRecyclerView.adapter?.notifyDataSetChanged()
                            //检查是否展示空布局
                            mManagerRecyclerView.setEmptyView(emptyView)
                        }
                    } ?: also {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        mCurrentPage--
                        //检查是否展示空布局
                        mManagerRecyclerView.setEmptyView(emptyView)
                    }
                }
            } catch (e: Exception) {
                //请求失败回退请求的页数
                if (mCurrentPage > 0) mCurrentPage--
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
                //检查是否展示空布局
                mManagerRecyclerView.setEmptyView(emptyView)
            }
        }, {
            //请求失败回退请求的页数
            if (mCurrentPage > 0) mCurrentPage--
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
            mManagerRecyclerView.setEmptyView(emptyView)
        })
    }

    class CustomAdapter(var dataList: ArrayList<CustomInfoModel>) :
        RecyclerView.Adapter<CustomItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomItemHolder =
            CustomItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.custom_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: CustomItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holder.itemView.context)
                .load(model.photo)
                .into(holder.avatar)
            holder.userNickname.text = model.nickname
            holder.mobile.text = model.mobile
        }

    }

    class CustomItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var avatar: ImageView = itemView.findViewById(R.id.avatar)
        var userNickname: TextView = itemView.findViewById(R.id.userNickname)
        var mobile: TextView = itemView.findViewById(R.id.mobile)
    }

}