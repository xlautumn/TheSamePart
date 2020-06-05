package com.same.part.assistant.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.data.model.CartProduct
import com.same.part.assistant.data.model.PayResult
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import com.same.part.assistant.viewmodel.request.RequestCreateOrderViewModel
import kotlinx.android.synthetic.main.order_settlement_activity.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.shaohui.bottomdialog.BottomDialog
import kotlin.math.log

class OrderSettlementActivity : AppCompatActivity() {
    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }
    private val requestCreateOrderViewModel: RequestCreateOrderViewModel by lazy { getViewModel<RequestCreateOrderViewModel>() }

    private val SDK_PAY_FLAG = 1

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    val payResult =
                        PayResult(msg.obj as Map<String?, String?>)

                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    val resultInfo: String = payResult.getResult() // 同步返回需要验证的信息
                    val resultStatus: String = payResult.getResultStatus()
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showShort(payResult.toString())
                        Log.i("resultInfo",resultInfo)

                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShort(payResult.toString())
                    }
                }
                else -> {
                }
            }
        }
    }

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
        tv_confirm.setOnClickListener { commitOrder() }
        orderRecyclerView.apply {
            adapter = OrderAdapter(requestCartViewModel.getCartList())
        }

        requestCreateOrderViewModel.createOrderResult.observe(this, Observer {
            requestCreateOrderViewModel.getPaySign(
                it.content.productOrders[0].productOrderId,
                "支付宝"
            )
        })

        requestCreateOrderViewModel.getPaySignResult.observe(this, Observer {

            alipay(it.content)

        })
    }

    private fun alipay(orderInfo: String) {

        val payRunnable = Runnable {
            val alipay = PayTask(this)
            val result =
                alipay.payV2(orderInfo, true)
            Log.i("msp", result.toString())
            val msg = Message()
            msg.what = SDK_PAY_FLAG
            msg.obj = result
            mHandler.sendMessage(msg)
        }

        // 必须异步调用
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    private fun commitOrder() {
        val cartIds = requestCartViewModel.cartIds
        requestCreateOrderViewModel.createOrder(cartIds)
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

