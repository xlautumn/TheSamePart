package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddVipCardActivity.Companion.CARD_DATA
import com.same.part.assistant.activity.AddVipCardActivity.Companion.HAND_CARD_SUCCESS
import com.same.part.assistant.activity.AddVipCardActivity.Companion.JUMP_FORM_ADD
import com.same.part.assistant.activity.AddVipCardActivity.Companion.JUMP_FORM_EDIT
import com.same.part.assistant.activity.AddVipCardActivity.Companion.JUMP_FORM_TYPE
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.MemberCardModel
import com.same.part.assistant.viewmodel.request.RequestVipCardViewModel
import kotlinx.android.synthetic.main.activity_vip_card_manager.*
import kotlinx.android.synthetic.main.activity_vip_card_manager.mSmartRefreshLayout
import kotlinx.android.synthetic.main.activity_vip_card_manager.mTitleBack
import kotlinx.android.synthetic.main.activity_vip_card_manager.mToolbarAdd
import kotlinx.android.synthetic.main.activity_vip_card_manager.mToolbarTitle
import me.hgj.jetpackmvvm.ext.getViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 会员卡管理
 */
class VipCardManagerActivity : AppCompatActivity() {

    //适配器
    private val mCardAdapter: CardAdapter by lazy { CardAdapter(arrayListOf()) }

    private val mRequestVipCardViewModel: RequestVipCardViewModel by lazy { getViewModel<RequestVipCardViewModel>() }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRefresh(messageEvent: String) {
        if (HAND_CARD_SUCCESS == messageEvent) {
            mSmartRefreshLayout.autoRefresh()
            mToolbarAdd.isEnabled = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip_card_manager)
        EventBus.getDefault().register(this)
        //标题
        mToolbarTitle.text = "会员卡管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //添加会员卡
        mToolbarAdd.setOnClickListener {
            startActivity(
                Intent(
                    this@VipCardManagerActivity,
                    AddVipCardActivity::class.java
                ).apply {
                    putExtra(JUMP_FORM_TYPE, JUMP_FORM_ADD)
                }
            )
        }
        //列表数据
        mVipRecyclerView.apply {
            adapter = mCardAdapter
            layoutManager = LinearLayoutManager(context)
        }
        //刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mRequestVipCardViewModel.getMemberCardList(true)
            }
            //上拉加载
            setOnLoadMoreListener {
                mRequestVipCardViewModel.getMemberCardList(false)
            }
        }
        //适配器
        mCardAdapter.apply {
            addChildClickViewIds(R.id.editCard)
            setOnItemChildClickListener { adapter, view, position ->
                startActivity(
                    Intent(
                        this@VipCardManagerActivity,
                        AddVipCardActivity::class.java
                    ).apply {
                        putExtra(CARD_DATA, mCardAdapter.data[position])
                        putExtra(JUMP_FORM_TYPE, JUMP_FORM_EDIT)
                    }
                )
            }
        }
        //获取会员卡列表数据
        mRequestVipCardViewModel.getMemberCardList(true)
        mRequestVipCardViewModel.cardsListResult.observe(this, Observer {
            when {
                it.isRefresh -> {
                    mCardAdapter.setNewInstance(it.memberCardList)
                    mSmartRefreshLayout.finishRefresh()
                    if (it.memberCardList.size > 0) {
                        mToolbarAdd.isEnabled = false
                    }
                }
                it.hasMore -> {
                    mCardAdapter.addData(it.memberCardList)
                    mSmartRefreshLayout.finishLoadMore()
                }
                else -> mSmartRefreshLayout.finishLoadMoreWithNoMoreData()
            }
        })
    }

    inner class CardAdapter(data: ArrayList<MemberCardModel>) :
        BaseQuickAdapter<MemberCardModel, BaseViewHolder>(R.layout.vip_card_info_item, data) {

        override fun convert(holder: BaseViewHolder, item: MemberCardModel) {
            //赋值
            item.run {
                holder.setText(R.id.vipCardName, name)
                holder.setText(R.id.vipCardDiscount, discount.toString())
                holder.setText(R.id.vipCardUserCount, userCount.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}