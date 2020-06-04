package com.same.part.assistant.activity

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.data.model.CartProduct
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import kotlinx.android.synthetic.main.order_settlement_activity.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.shaohui.bottomdialog.BottomDialog

class OrderSettlementActivity : AppCompatActivity() {
    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_settlement_activity)
        //标题
        mToolbarTitle.text = "提交订单"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

        ll_payment.setOnClickListener { showPaymentDialog() }
        orderRecyclerView.apply {
            adapter = OrderAdapter(requestCartViewModel.getCartList())
        }
    }

    private fun showPaymentDialog() {
        BottomDialog.create(supportFragmentManager)
            .setLayoutRes(R.layout.select_payment_dialog)
            .setViewListener {
                it.findViewById<RadioGroup>(R.id.radio_payment)
                    .setOnCheckedChangeListener { group, checkedId ->
                        val radioButton = findViewById<RadioButton>(checkedId)
                    }
            }
            .show()

    }


    class OrderAdapter(private val data: List<CartProduct>) :
        RecyclerView.Adapter<OrderViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            return OrderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cashier_order_good_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val model = data[position]
            val product = model.shopProduct.productDetailData
            Glide.with(holder.itemView.context).load(product.img)
                .into(holder.goodAvatar)
            holder.goodName.text = product.name
            holder.goodNum.text = "x${model.shopProduct.num}"
//            if (product.oldPrice.isEmpty()) {
//                holder.goodPriceOld.visibility = View.GONE
//            } else {
//                holder.goodPriceOld.apply {
//                    visibility = View.VISIBLE
//                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
//                    text = model.oldPrice
//                }
//
//            }
            holder.goodPriceNew.text = product.price
        }

    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPriceOld: TextView = itemView.findViewById(R.id.goodPriceOld)
        var goodPriceNew: TextView = itemView.findViewById(R.id.goodPriceNew)
    }
}

