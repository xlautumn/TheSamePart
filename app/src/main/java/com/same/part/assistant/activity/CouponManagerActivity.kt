package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.activity.AddCouponActivity.Companion.ADD_COUPON_SUCCESS
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.databinding.ActivityCouponManagerBinding
import com.same.part.assistant.helper.refreshComplete
import com.same.part.assistant.viewmodel.request.RequestCouponsViewModel
import kotlinx.android.synthetic.main.activity_coupon_manager.*
import kotlinx.android.synthetic.main.activity_coupon_manager.mSmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_cashier.*
import kotlinx.android.synthetic.main.fragment_product_classification.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 优惠券管理
 */
class CouponManagerActivity :
    BaseActivity<BaseViewModel, ActivityCouponManagerBinding>() {

    //适配器
    private lateinit var mCouponAdapter: CouponAdapter

    private val mRequestCouponsViewModel: RequestCouponsViewModel by lazy { getViewModel<RequestCouponsViewModel>() }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventRefresh(messageEvent: String) {
        if (ADD_COUPON_SUCCESS == messageEvent) {
            mSmartRefreshLayout.autoRefresh()
        }
    }

    override fun layoutId(): Int = R.layout.activity_coupon_manager

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        //标题
        mToolbarTitle.text = "优惠券管理"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //添加优惠券
        mToolbarAdd.setOnClickListener {
            startActivity(Intent(this@CouponManagerActivity, AddCouponActivity::class.java))
        }
        //列表数据
        mCouponAdapter = CouponAdapter(arrayListOf())
        mCouponRecyclerView.apply {
            adapter = mCouponAdapter
            layoutManager = LinearLayoutManager(context)
        }
        //刷新
        mSmartRefreshLayout.apply {
            //下拉刷新
            setOnRefreshListener {
                mRequestCouponsViewModel.getCouponsList()
            }
        }
        //获取优惠券数据
        mRequestCouponsViewModel.getCouponsList()
    }

    override fun createObserver() {
        mRequestCouponsViewModel.couponsListResult.observe(this, Observer {
            mCouponAdapter.setNewInstance(it)
            mSmartRefreshLayout.refreshComplete()
        })
    }

    class CouponAdapter(data: ArrayList<CouponInfoModel>) :
        BaseQuickAdapter<CouponInfoModel, BaseViewHolder>(R.layout.coupon_info_item, data) {

        override fun convert(holder: BaseViewHolder, item: CouponInfoModel) {
            //赋值
            item.run {
                holder.setText(R.id.couponName, name)
                holder.setText(R.id.couponCount, "$issued/$remain")
                holder.setText(R.id.couponUse, used)
                holder.setText(R.id.couponStatus, statements)
                holder.setTextColor(
                    R.id.couponStatus, when (status) {
                        "0" -> 0xFF0EB170.toInt()
                        "1" -> 0xFFE6660F.toInt()
                        else -> 0xFF999999.toInt()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}