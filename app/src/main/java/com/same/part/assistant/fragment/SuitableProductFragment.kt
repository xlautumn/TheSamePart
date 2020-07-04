package com.same.part.assistant.fragment

import android.os.Bundle
import android.widget.ImageView
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
import com.same.part.assistant.data.model.CashierProduct
import com.same.part.assistant.databinding.FragmentSuitableProductBinding
import com.same.part.assistant.viewmodel.request.RequestProvideCouponViewModel
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import me.hgj.jetpackmvvm.ext.getViewModel

/**
 * 适用商品
 */
class SuitableProductFragment :
    BaseFragment<RequestProvideCouponViewModel, FragmentSuitableProductBinding>() {

    private lateinit var suitableProductViewModel: SuitableProductViewModel
    private val mAdapter = CashierProductAdapter()
    override fun layoutId(): Int = R.layout.fragment_suitable_product

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.titleBack.setOnClickListener {
            activity?.onBackPressed()
        }
        mDatabind.toolbarSelect.setOnClickListener {
            findNavController().navigate(R.id.action_suitableProductFragment_to_selectSuitableProductFragment)
        }
        mDatabind.tvConfirm.setOnClickListener {
            if (suitableProductViewModel.suitableProductList.value.isNullOrEmpty()) {
                ToastUtils.showShort("您还未选择商品")
            } else {
                activity?.onBackPressed()
            }
        }
        mDatabind.emptyView.apply {
            this.findViewById<ImageView>(R.id.iv_empty_icon)
                .setImageResource(R.drawable.icon_empty_product)
            this.findViewById<TextView>(R.id.tv_empty_reminder).text = "请选择商品"
        }
        mDatabind.rvProviderCustomer.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            setEmptyView(mDatabind.emptyView)
        }
        mAdapter.addChildClickViewIds(R.id.iv_del)
        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.iv_del) {
                val product = (adapter.data[position] as CashierProduct)
                suitableProductViewModel.delProduct(product)
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {
            suitableProductViewModel = getViewModel<SuitableProductViewModel>()
            suitableProductViewModel.suitableProductList.observe(viewLifecycleOwner, Observer {
                mAdapter.setList(it)
            })
        }
    }

    override fun lazyLoadData() {

    }

    class CashierProductAdapter() :
        BaseQuickAdapter<CashierProduct, BaseViewHolder>(R.layout.layout_item_suitable_product, null) {

        override fun convert(holder: BaseViewHolder, item: CashierProduct) {
            holder.getView<ImageView>(R.id.photo).let {
                if (item.img.isNullOrEmpty()){
                    it.setImageResource(R.drawable.icn_head_img)
                }else {
                    Glide.with(holder.itemView.context)
                        .load(it.appendImageScaleSuffix(item.img ?: ""))
                        .into(it)
                }
            }
            holder.setText(R.id.name, item.name)
            holder.setText(R.id.tv_price, "￥${item.price}")
            holder.setText(R.id.tv_quantity, "库存${item.quantity}${item.unit}")
        }
    }
}