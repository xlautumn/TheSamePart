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
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.PAYMENT_CATEGORY_FREIGHT_COLLECT
import com.same.part.assistant.data.PAYMENT_CATEGORY_ONLINE_PAY
import com.same.part.assistant.data.PAYMENT_CHANNEL_ALIPAY
import com.same.part.assistant.data.PAYMENT_CHANNEL_WECHAT
import com.same.part.assistant.data.model.CartProduct
import com.same.part.assistant.data.model.PayResult
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import com.same.part.assistant.viewmodel.request.RequestCreateOrderViewModel
import com.same.part.assistant.viewmodel.request.RequestPaySignOrderInfoViewModel
import kotlinx.android.synthetic.main.activity_order_submit.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.base.activity.BaseVmActivity
import me.hgj.jetpackmvvm.ext.getAppViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.ext.parseState
import me.shaohui.bottomdialog.BottomDialog

class OrderSubmitActivity : BaseVmActivity<RequestCreateOrderViewModel>(),
    View.OnClickListener {
    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }
    private val requestPaySignOrderInfoViewModel: RequestPaySignOrderInfoViewModel by lazy { getViewModel<RequestPaySignOrderInfoViewModel>() }
    private var processDialog: AppCompatDialog? = null
    private val SDK_PAY_FLAG = 1

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SDK_PAY_FLAG -> {
                    dismissLoading()
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
                        mViewModel.onPaySuccess()
                        requestPaySignOrderInfoViewModel.onPaySuccess()
                        ToastUtils.showShort(payResult.toString())
                        Log.i("resultInfo", resultInfo)
                        finish()

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

    override fun layoutId(): Int = R.layout.activity_order_submit

    override fun initView(savedInstanceState: Bundle?) {
        mToolbarTitle.text = "提交订单"
        mTitleBack.setOnClickListener {
            finish()
        }
        ll_payment.setOnClickListener(this)
        tv_confirm.setOnClickListener(this)

        orderRecyclerView.apply {
            adapter = OrderAdapter(requestCartViewModel.getCartList())
        }
        tv_total_money.text = "￥${requestCartViewModel.totalPrice}"
        orderPayment.text = mViewModel.paymentCategory
        shippingAddress.text = CacheUtil.getDetailAddress()
        CacheUtil.getAddress()?.let {
            shippingPerson.text = "${it.user.realname}    ${it.user.mobile}"
        }

        mViewModel.clearData()
        requestPaySignOrderInfoViewModel.clearData()
    }

    override fun createObserver() {
        mViewModel.createOrderResult.observe(this, Observer {

            parseState(it,
                onSuccess = {
                    requestCartViewModel.onCreateOrderSuccess()
                    if (mViewModel.isFreightCollect()) {
                        finish()
                    } else {
                        showPaymentChannel(it.productOrders[0].productOrderId)
                    }
                },
                onError = {
                    ToastUtils.showShort(it.errorMsg)
                }
            )
        })

        requestPaySignOrderInfoViewModel.getPaySignResult.observe(this, Observer {
            parseState(it,
                onSuccess = { content ->
                    if (content.isNotEmpty()) {
                        alipay(content)
                    }
                },
                onError = { appException ->
                    ToastUtils.showShort(appException.errorMsg)
                }
            )
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_payment -> {
                showPaymentDialog()
            }

            R.id.tv_confirm -> {
                submitOrder()
            }
        }
    }


    override fun showLoading(message: String) {
        if (processDialog == null) {
            processDialog = this.let {

                AppCompatDialog(it).apply {
                    setCancelable(true)
                    setCanceledOnTouchOutside(false)
                    setContentView(R.layout.layout_pay_progress_dialog_view)

                }

            }
        }
        processDialog?.window?.run {
            this.findViewById<TextView>(R.id.loading_tips).text = message
        }
        processDialog?.show()
    }

    override fun dismissLoading() {
        processDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        processDialog?.dismiss()
    }


    /**
     * 支付宝支付
     */
    private fun alipay(orderInfo: String) {

        showLoading("正在支付...")
        val payRunnable = Runnable {
            val alipay = PayTask(this)
            val result =
                alipay.payV2(orderInfo, false)
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

    /**
     * 提交订单
     */
    private fun submitOrder() {

        val orderId = mViewModel.getOrderId()
        if (TextUtils.isEmpty(orderId)) {
            val cartIds = requestCartViewModel.cartIds
            mViewModel.createOrder(cartIds)
        } else {
            showPaymentChannel(orderId)
        }

    }

    /**
     * 选择付款方式弹窗
     */
    private fun showPaymentDialog() {
        fun handleRadioClick(dialog: BottomDialog, radioButton: RadioButton) {
            val paymentCategory = radioButton.text.toString()
            orderPayment.text = paymentCategory
            mViewModel.paymentCategory = paymentCategory
            dialog.dismiss()
        }

        val dialog = BottomDialog.create(supportFragmentManager)
            .setLayoutRes(R.layout.select_payment_dialog)
        dialog.setViewListener {
            it.findViewById<RadioButton>(R.id.rb_1).apply {
                text = PAYMENT_CATEGORY_ONLINE_PAY
                isChecked = mViewModel.paymentCategory == text
            }.setOnClickListener { radioButton ->
                handleRadioClick(
                    dialog,
                    radioButton as RadioButton
                )
            }
            it.findViewById<RadioButton>(R.id.rb_2).apply {
                text = PAYMENT_CATEGORY_FREIGHT_COLLECT
                isChecked = mViewModel.paymentCategory == text
            }.setOnClickListener { radioButton ->
                handleRadioClick(
                    dialog,
                    radioButton as RadioButton
                )
            }
            it.findViewById<Button>(R.id.bt_cancel).setOnClickListener { dialog.dismiss() }

        }.show()

    }


    /**
     * 选择付款渠道弹窗
     */
    private fun showPaymentChannel(orderId: String) {
        fun handleRadioClick(dialog: BottomDialog, radioButton: RadioButton) {
            val paymentChanel = radioButton.text.toString()
            requestPaySignOrderInfoViewModel.paymentChannel = paymentChanel
            requestPaySignOrderInfoViewModel.getPaySign(orderId)
            dialog.dismiss()
        }
        val dialog = BottomDialog.create(supportFragmentManager)
            .setLayoutRes(R.layout.select_payment_dialog)
        dialog.setViewListener {
            it.findViewById<RadioButton>(R.id.rb_1).apply {
                text = PAYMENT_CHANNEL_ALIPAY
                isChecked = requestPaySignOrderInfoViewModel.paymentChannel == text
            }.setOnClickListener { radioButton ->
                handleRadioClick(
                    dialog,
                    radioButton as RadioButton
                )
            }
            it.findViewById<RadioButton>(R.id.rb_2).apply {
                text = PAYMENT_CHANNEL_WECHAT
                isChecked = requestPaySignOrderInfoViewModel.paymentChannel == text
            }.setOnClickListener { radioButton ->
                handleRadioClick(
                    dialog,
                    radioButton as RadioButton
                )
            }
            it.findViewById<Button>(R.id.bt_cancel).setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

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
        val cartProduct = data[position]
        val product = cartProduct.shopProduct.productDetailData
        Glide.with(holder.itemView.context).load(product.img)
            .into(holder.goodAvatar)
        holder.goodName.text = product.name
        holder.goodNum.text = "x${cartProduct.shopProduct.num}"
        holder.goodPriceNew.text = cartProduct.price
        val tags = cartProduct.getProperties().joinToString(separator = "/")
        if (tags.isNullOrEmpty()) {
            holder.goodTag.visibility = View.GONE
        } else {
            holder.goodTag.visibility = View.VISIBLE
            holder.goodTag.text = tags
        }
    }
}

class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
    var goodName: TextView = itemView.findViewById(R.id.goodName)
    var goodNum: TextView = itemView.findViewById(R.id.goodNum)
    var goodPriceOld: TextView = itemView.findViewById(R.id.goodPriceOld)
    var goodPriceNew: TextView = itemView.findViewById(R.id.goodPriceNew)
    val goodTag: TextView = itemView.findViewById(R.id.goodTag)
}