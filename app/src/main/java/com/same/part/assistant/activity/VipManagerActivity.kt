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
import com.same.part.assistant.data.model.VipInfoModel
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.utils.HttpUtil
import kotlinx.android.synthetic.main.activity_vip_manager.*
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 会员管理
 */
class VipManagerActivity : AppCompatActivity() {
    //当前请求第几页的数据
    private var mCurrentPage = 0

    private val mVipList = ArrayList<VipInfoModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_manager)
        //标题
        mToolbarTitle.text = "会员管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //列表数据
        mManagerRecyclerView.apply {
            adapter = CustomAdapter(mVipList)
            layoutManager = LinearLayoutManager(context)
        }
        //下拉刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mCurrentPage = 0
                loadVipList(page = mCurrentPage, isRefresh = true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mCurrentPage++
                loadVipList(page = mCurrentPage, isRefresh = false)
            }
        }
        //请求收银商品列表
        loadVipList(page = mCurrentPage, isRefresh = true)
    }

    private fun loadVipList(
        page: Int = 0,
        size: String = "20",
        isRefresh: Boolean
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}user-cards")
            .append("?shopId=${CacheUtil.getShopId()}")
            .append("&page=$page")
            .append("&size=$size")
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
                        } else {
                            val vipList = ArrayList<VipInfoModel>()
                            for (i in 0 until this.size) {
                                getJSONObject(i)?.apply {
                                    var name = ""
                                    var discount = ""
                                    var photo = ""
                                    var mobile = ""
                                    getJSONObject("card")?.apply {
                                        name = getString("name")
                                        discount = getString("discount")
                                    }
                                    getJSONObject("user")?.apply {
                                        photo = getString("photo")
                                        mobile = getString("mobile")
                                    }
                                    VipInfoModel(name, photo, discount, mobile).apply {
                                        vipList.add(this)
                                    }
                                }
                            }
                            //如果是刷新需要清空之前的数据
                            if (isRefresh && vipList.size > 0) {
                                mVipList.clear()
                                mVipList.addAll(vipList)
                            } else {
                                mVipList.addAll(vipList)
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
            //检查是否展示空布局
            mManagerRecyclerView.setEmptyView(emptyView)
        })

    }


    class CustomAdapter(var dataList: ArrayList<VipInfoModel>) :
        RecyclerView.Adapter<VipItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VipItemHolder =
            VipItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.vip_info_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: VipItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holder.itemView.context)
                .load(model.photo)
                .into(holder.photo)
            holder.name.text = model.name
            holder.discount.text = model.discount
            holder.mobile.text = model.mobile
            if (position == dataList.size - 1) holder.divider.visibility = View.GONE
        }

    }

    class VipItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var discount: TextView = itemView.findViewById(R.id.discount)
        var photo: ImageView = itemView.findViewById(R.id.photo)
        var mobile: TextView = itemView.findViewById(R.id.mobile)
        var divider: View = itemView.findViewById(R.id.item_divider)
    }
}