package com.same.part.assistant.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.data.model.CouponInfoModel
import com.same.part.assistant.databinding.ActivityCouponManagerBinding
import com.same.part.assistant.viewmodel.request.RequestCouponsViewModel
import kotlinx.android.synthetic.main.activity_coupon_manager.*
import me.hgj.jetpackmvvm.ext.getViewModel

/**
 * 优惠券管理
 */
class CouponManagerActivity :
    BaseActivity<RequestCouponsViewModel, ActivityCouponManagerBinding>() {

    //适配器
    private lateinit var mCouponAdapter: CouponAdapter

    private val mRequestCouponsViewModel: RequestCouponsViewModel by lazy { getViewModel<RequestCouponsViewModel>() }

    override fun layoutId(): Int = R.layout.activity_coupon_manager

    override fun initView(savedInstanceState: Bundle?) {
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
        //获取优惠券数据
        mRequestCouponsViewModel.getCouponsList()
    }

    override fun createObserver() {
        mRequestCouponsViewModel.couponsListResult.observe(this, Observer {
            mCouponAdapter.setNewInstance(it)
        })
    }

    class CouponAdapter(data: ArrayList<CouponInfoModel>) : BaseQuickAdapter<CouponInfoModel, BaseViewHolder>(R.layout.coupon_info_item, data) {

        override fun convert(holder: BaseViewHolder, item: CouponInfoModel) {
            //赋值
            item.run {
                holder.setText(R.id.couponName,name )
                holder.setText(R.id.couponCount, "$issued/$remain")
                holder.setText(R.id.couponUse, used)
                holder.setText(R.id.couponStatus, statements)
                holder.setTextColor(R.id.couponStatus, when (status) {
                    "0" -> 0xFF0EB170.toInt()
                    "1" -> 0xFFE6660F.toInt()
                    else -> 0xFF999999.toInt()
                })
            }
        }
    }

}