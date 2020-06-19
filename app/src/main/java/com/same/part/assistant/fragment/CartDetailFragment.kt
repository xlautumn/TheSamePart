package com.same.part.assistant.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.activity.OrderSubmitActivity
import com.same.part.assistant.adapter.CartProductAdapter
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.databinding.FragmentCartDetailBinding
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import me.hgj.jetpackmvvm.ext.getAppViewModel

class CartDetailFragment: Fragment(), View.OnClickListener {

    private lateinit var binding:FragmentCartDetailBinding
    private val mCartProductAdapter: CartProductAdapter by lazy { CartProductAdapter(ProxyClick()) }
    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartDetailBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statement.setOnClickListener(this)
        binding.rootDetail.setOnClickListener(this)
        binding.rightBtn.setOnClickListener(this)
        binding.cartInvalidView.setOnClickListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.popListView.apply {
            adapter = mCartProductAdapter
        }

        requestCartViewModel.cartProductList.observe(viewLifecycleOwner, Observer {
            mCartProductAdapter.setData(it)
            binding.totalMoney.text = requestCartViewModel.totalPrice
            binding.statement.text = getString(R.string.purchase_statement, requestCartViewModel.totalNum)
            if (it.isEmpty()) {
                binding.popListView.visibility = View.INVISIBLE
                binding.tvNoData.visibility = View.VISIBLE
                if (binding.cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            } else {
                binding.popListView.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.INVISIBLE
            }
        })

        requestCartViewModel.requestCartList()
    }
    override fun onClick(v: View) {
        when (v?.id) {
            R.id.statement -> {
                if (requestCartViewModel.cartProductList.value.isNullOrEmpty()) {
                    ToastUtils.showShort("还未添加到购物车")
                } else {
                    startActivity(Intent(context, OrderSubmitActivity::class.java))
                }
            }
            R.id.rootDetail -> {
                binding.cartDetailArrow.rotation = 0f
                if (binding.cartDetailList.visibility == View.GONE) {
                    showCartDetail()

                } else if (binding.cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            }
            R.id.right_btn -> {
                //清空购物车
                requestCartViewModel.clearCarts()
            }
            R.id.cart_invalid_view -> {
                if (binding.cartDetailList.visibility == View.VISIBLE) {
                    hideCartDetail()
                }
            }
        }
    }

    /**
     * 隐藏购物车详情
     */
    private fun hideCartDetail() {
        val hidden = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f
        )
        hidden.duration = PurchaseFragment.ANIMATION_DURATION
        binding.cartDetailList.startAnimation(hidden)
        binding.cartDetailList.visibility = View.GONE
        binding.cartDetailArrow.rotation = 0f
    }

    /**
     * 显示购物车详情
     */
    private fun showCartDetail() {
        val show = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.0f
        )
        show.duration = PurchaseFragment.ANIMATION_DURATION
        binding.cartDetailList.startAnimation(show)
        binding.cartDetailList.visibility = View.VISIBLE
        binding.cartDetailArrow.rotation = -90f
    }



    inner class ProxyClick {
        /**
         * 添加购物车
         */
        fun addShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.addShopProduct(shopProduct)
        }

        /**
         * 减少购物车数量
         */
        fun minusShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.minusShopProduct(shopProduct)
        }
    }
}