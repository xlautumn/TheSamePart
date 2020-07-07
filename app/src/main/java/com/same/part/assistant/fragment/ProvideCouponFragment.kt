package com.same.part.assistant.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.app.ext.appendImageScaleSuffix
import com.same.part.assistant.data.model.Customer
import com.same.part.assistant.databinding.FragmentProvideCouponBinding
import com.same.part.assistant.viewmodel.request.RequestProvideCouponViewModel
import com.same.part.assistant.viewmodel.state.ProvideCouponViewModel
import kotlinx.android.synthetic.main.activity_channel.*
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import me.shaohui.bottomdialog.BottomDialog

class ProvideCouponFragment :
    BaseFragment<RequestProvideCouponViewModel, FragmentProvideCouponBinding>() {

    private lateinit var provideCouponViewModel: ProvideCouponViewModel
    private val mAdapter = CustomerAdapter()
    override fun layoutId(): Int = R.layout.fragment_provide_coupon

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.titleBack.setOnClickListener {
            activity?.finish()
        }
        mDatabind.toolbarSelect.setOnClickListener { showCustomerTypeDialog() }
        mDatabind.tvProvide.setOnClickListener {
            if (provideCouponViewModel.customerListToProvider.value.isNullOrEmpty()){
                ToastUtils.showShort("您还未选择发放客户")
            }else {
                provideCouponViewModel.sendCoupon(onSuccess = {
                    ToastUtils.showShort(it)

                    activity?.apply {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                },onError = {
                    ToastUtils.showShort(it)
                })
            }
        }
        mDatabind.emptyView.apply {
            this.findViewById<ImageView>(R.id.iv_empty_icon).setImageResource(R.drawable.icon_custom_to_provider)
            this.findViewById<TextView>(R.id.tv_empty_reminder).text = "请选择发放用户"
        }
        mDatabind.rvProviderCustomer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            setEmptyView(mDatabind.emptyView)
        }
        mAdapter.addChildClickViewIds(R.id.iv_del)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_del) {
                val customer = (adapter.data[position] as Customer)
                provideCouponViewModel.delCustomToProvider(customer)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {
            provideCouponViewModel = getViewModel<ProvideCouponViewModel>()
            provideCouponViewModel.customerListToProvider.observe(viewLifecycleOwner, Observer {
                mAdapter.setList(it)
            })

            provideCouponViewModel.couponSendResultState.observe(viewLifecycleOwner, Observer {
                parseState(it,onSuccess = {

                },onError = {
                    ToastUtils.showShort(it.errorMsg)
                })
            })
        }
    }

    override fun lazyLoadData() {

    }


    /**
     * 显示选择客户类型对话框
     */
    private fun showCustomerTypeDialog() {
        val dialog = BottomDialog.create(childFragmentManager)
        dialog.setViewListener {
            handleDialogView(it, dialog)
        }.setLayoutRes(R.layout.dialog_select_customer_type).setDimAmount(0.4F)
            .setCancelOutside(true).setTag("showCustomerTypeDialog").show()
    }

    private fun handleDialogView(
        it: View,
        dialog: BottomDialog
    ) {
        val radioGeneral = it.findViewById<RadioButton>(R.id.radio_general)
        val radioMember = it.findViewById<RadioButton>(R.id.radio_member)
        var customerType = SelectCustomFragment.CUSTOMER_TYPE_GENERAL
        radioGeneral.isChecked = true
        it.findViewById<TextView>(R.id.tv_confirm).setOnClickListener {
            when {
                radioGeneral.isChecked -> customerType = SelectCustomFragment.CUSTOMER_TYPE_GENERAL
                radioMember.isChecked -> customerType = SelectCustomFragment.CUSTOMER_TYPE_MEMBER
            }

            val bundle = Bundle()
            bundle.putInt(SelectCustomFragment.KEY_CUSTOMER_TYPE, customerType)
            findNavController().navigate(
                R.id.action_provideCouponFragment_to_selectCustomFragment,
                bundle
            )
            dialog.dismissAllowingStateLoss()
        }
    }

    class CustomerAdapter() :
        BaseQuickAdapter<Customer, BaseViewHolder>(R.layout.customer_to_provider_item, null) {

        override fun convert(holder: BaseViewHolder, item: Customer) {
            holder.getView<ImageView>(R.id.photo).let {
                Glide.with(holder.itemView.context)
                    .load(it.appendImageScaleSuffix(item.photo))
                    .into(it)
            }
            holder.setText(R.id.name, item.nickname)
            holder.setText(R.id.mobile, item.mobile)
        }
    }
}