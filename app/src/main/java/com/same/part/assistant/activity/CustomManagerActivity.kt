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
                loadCustomManagerList()
            }
            //上拉加载
            setOnLoadMoreListener {
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
            }
        }
        //加载客户管理列表
        loadCustomManagerList()
    }

    private fun loadCustomManagerList() {
        val url = StringBuilder("${ApiService.SERVER_URL}biz/customers")
            .append("?shopId=${CacheUtil.getShopId()}")
            .append("&appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
        HttpUtil.instance.getUrl(url.toString(), {
            try {
                val resultJson = JSONObject.parseObject(it)
                resultJson.apply {
                    getJSONArray("content")?.takeIf { array -> array.size > 0 }?.apply {
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
                        if (listItems.size > 0) {
                            mCustomList.clear()
                            mCustomList.addAll(listItems)
                        }
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                        //刷新数据
                        mManagerRecyclerView.adapter?.notifyDataSetChanged()
                    } ?: also {
                        //通知刷新结束
                        mSmartRefreshLayout?.refreshComplete(false)
                    }
                }
            } catch (e: Exception) {
                //通知刷新结束
                mSmartRefreshLayout?.refreshComplete(false)
            }
        }, {
            //通知刷新结束
            mSmartRefreshLayout?.refreshComplete(true)
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