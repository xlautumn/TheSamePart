package com.same.part.assistant.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.databinding.FragmentProvideCouponBinding
import com.same.part.assistant.viewmodel.request.RequestProvideCouponViewModel

class ProvideCouponFragment :
    BaseFragment<RequestProvideCouponViewModel, FragmentProvideCouponBinding>() {
    override fun layoutId(): Int = R.layout.fragment_provide_coupon

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.titleBack.setOnClickListener {
            activity?.finish()
        }
        mDatabind.toolbarSelect.setOnClickListener { findNavController().navigate(R.id.action_provideCouponFragment_to_selectCustomFragment) }
        mDatabind.tvProvide.setOnClickListener {
            ToastUtils.showShort("发放")
        }
    }

    override fun lazyLoadData() {

    }
}