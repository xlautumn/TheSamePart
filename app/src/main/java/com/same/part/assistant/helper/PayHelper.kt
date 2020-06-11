package com.same.part.assistant.helper

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alipay.sdk.app.PayTask
import com.blankj.utilcode.util.ToastUtils
import com.same.part.assistant.R
import com.same.part.assistant.data.PAYMENT_CHANNEL_ALIPAY
import com.same.part.assistant.data.PAYMENT_CHANNEL_WECHAT
import com.same.part.assistant.data.model.PayResult
import com.same.part.assistant.viewmodel.request.RequestPaySignOrderInfoViewModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.getViewModel
import me.hgj.jetpackmvvm.network.AppException
import me.hgj.jetpackmvvm.state.ResultState
import me.shaohui.bottomdialog.BottomDialog

class PayHelper(private val activity: AppCompatActivity) {
    private val requestPaySignOrderInfoViewModel: RequestPaySignOrderInfoViewModel by lazy { activity.getViewModel<RequestPaySignOrderInfoViewModel>() }
    private var processDialog: AppCompatDialog? = null
    private val SDK_PAY_FLAG = 1
    private val payResultViewModel: PayResultViewModel by lazy { activity.getViewModel<PayResultViewModel>() }
    var paymentChannel = PAYMENT_CHANNEL_ALIPAY
    init {
        requestPaySignOrderInfoViewModel.clearData()
        createObserver()
    }

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
                        requestPaySignOrderInfoViewModel.onPaySuccess()
                        payResultViewModel.onPaySuccess()
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        payResultViewModel.onPayError(AppException(resultStatus,payResult.memo))
                    }
                }
                else -> {
                }
            }
        }
    }


    /**
     * 观察支付状态
     */
    fun observePayResultState(owner: LifecycleOwner, onSuccess:(String)-> Unit = {},onError:(String)->Unit = {},onLoading: ((String) -> Unit)= {} ){
        payResultViewModel.payResultState.observe(owner, Observer {
            when (it) {
                is ResultState.Loading -> {
                    onLoading.invoke(it.loadingMessage)
                }
                is ResultState.Success -> {
                    onSuccess(it.data)
                }
                is ResultState.Error -> {
                    onError(it.error.errorMsg)
                }
            }
        })
    }

    /**
     * 选择付款渠道弹窗
     */
    fun showPaymentChannel(orderId: String) {
        fun handleRadioClick(dialog: BottomDialog, radioButton: RadioButton) {
            val paymentChanel = radioButton.text.toString()
            this.paymentChannel = paymentChanel
            requestPaySignOrderInfoViewModel.getPaySign(orderId,this.paymentChannel)
            dialog.dismiss()
        }

        val dialog = BottomDialog.create(activity.supportFragmentManager)
            .setLayoutRes(R.layout.select_payment_channel_dialog)
        dialog.setViewListener {
            it.findViewById<RadioButton>(R.id.rb_1).apply {
                text = PAYMENT_CHANNEL_ALIPAY
                isChecked = paymentChannel == text
            }.setOnClickListener { radioButton ->
                handleRadioClick(
                    dialog,
                    radioButton as RadioButton
                )
            }
//            it.findViewById<RadioButton>(R.id.rb_2).apply {
//                text = PAYMENT_CHANNEL_WECHAT
//                isChecked = paymentChannel == text
//            }.setOnClickListener { radioButton ->
//                handleRadioClick(
//                    dialog,
//                    radioButton as RadioButton
//                )
//            }
            it.findViewById<Button>(R.id.bt_cancel).setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    private fun createObserver() {

        requestPaySignOrderInfoViewModel.getPaySignResult.observe(activity, Observer {
            parseState(it,
                onSuccess = { content ->
                    if (content.isNotEmpty()) {
                        if (paymentChannel == PAYMENT_CHANNEL_ALIPAY) {
                            alipay(content)
                        }else if (paymentChannel== PAYMENT_CHANNEL_WECHAT){
                            wechatPay(content)
                        }else{
                            ToastUtils.showShort("暂不支持该支付方式")
                        }
                    }
                },
                onError = { appException ->
                    ToastUtils.showShort(appException.errorMsg)
                }
            )
        })

        payResultViewModel.payResultState.observe(activity, Observer {
            parseState(it,
            onSuccess = { resultInfo ->
                ToastUtils.showShort("支付成功")
                Log.i("resultInfo", resultInfo)
            },
            onError = { appException ->
                ToastUtils.showShort(appException.errorMsg)
            })
        })

        val observer = PayLifecycleObserver(::dismissLoading)
        activity.lifecycle.addObserver(observer)
    }

    /**
     * 微信支付
     */
    private fun wechatPay(content: String) {
//        {"code":1,"msg":"weixin","content":{"sign":"427CD0763398F78D9747116D1EE1E783",
//            "prepayId":"wx07145428994032d45a89d48e1235099900",
//            "partnerId":"1521386741",
//            "appId":"wx631ec50e4a502946",
//            "packageValue":"Sign=WXPay",
//            "timeStamp":"1591512869",
//            "nonceStr":"1591512869062"}}
        ToastUtils.showShort("暂不支持微信支付方式")
    }

    private fun showLoading(message: String) {
        if (processDialog == null) {
            processDialog = this.let {

                AppCompatDialog(activity).apply {
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

    private fun dismissLoading() {
        processDialog?.dismiss()
    }


    /**
     * 支付宝支付
     */
    private fun alipay(orderInfo: String) {

        val loadingMsg = "正在支付..."
        payResultViewModel.onPayLoading(loadingMsg)
        val payRunnable = Runnable {
            val alipay = PayTask(activity)
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

    private fun <T> parseState(
        resultState: ResultState<T>,
        onSuccess: (T) -> Unit,
        onError: ((AppException) -> Unit)? = null,
        onLoading: (() -> Unit)? = null
    ) {
        when (resultState) {
            is ResultState.Loading -> {
                showLoading(resultState.loadingMessage)
                onLoading?.run { this }
            }
            is ResultState.Success -> {
                dismissLoading()
                onSuccess(resultState.data)
            }
            is ResultState.Error -> {
                dismissLoading()
                onError?.run { this(resultState.error) }
            }
        }
    }

}

class PayResultViewModel(application: Application) : BaseViewModel(application) {
    private var _payResult = MutableLiveData<ResultState<String>>()
    val payResultState: LiveData<ResultState<String>> get() = _payResult

    fun onPayLoading(loadingMsg: String = "正在支付") {
        _payResult.postValue(ResultState.onAppLoading(loadingMsg))
    }

    fun onPaySuccess(){
        _payResult.postValue(ResultState.onAppSuccess(""))
    }

    fun onPayError(error:AppException){
        _payResult.postValue(ResultState.onAppError(error))
    }
}