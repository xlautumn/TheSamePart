package com.same.part.assistant.activity

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.data.model.CashierGoodItemModel
import com.same.part.assistant.data.model.PurchaseOrderModel
import kotlinx.android.synthetic.main.activity_cashier_order_detail.orderRecyclerView
import kotlinx.android.synthetic.main.activity_purchase_order_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*


/**
 * 采购订单详情
 */
class PurchaseOrderDetailActivity : AppCompatActivity() {

    //订单对象类
    private lateinit var mPurchaseOrderModel: PurchaseOrderModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_order_detail)
        //标题
        mToolbarTitle.text = "订单详情"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }

        //获取数据
        intent?.getSerializableExtra(CashierOrderDetailActivity.ORDER_DETAIL_KEY)?.apply {
            if (this is PurchaseOrderModel) {
                mPurchaseOrderModel = this
                //订单状态
                if (mPurchaseOrderModel.state in listOf("1", "-1")) {
                    orderStatement.setTextColor(0xFF999999.toInt())
                } else {
                    orderStatement.setTextColor(0xFFE76612.toInt())
                }
                orderStatement.text = mPurchaseOrderModel.statements
                //收货地址
                shippingAddress.text =
                    "${mPurchaseOrderModel.province}${mPurchaseOrderModel.city}${mPurchaseOrderModel.district}${mPurchaseOrderModel.address}"
                //联系人
                shippingPerson.text =
                    "${mPurchaseOrderModel.addrName}    ${mPurchaseOrderModel.addrMobile}"
                //支付方式
                orderPayment.text = mPurchaseOrderModel.payment
                //合计
                orderAmount.text = "￥${mPurchaseOrderModel.price}"
                orderTotal.text = "￥${mPurchaseOrderModel.price}"
                //配送员
                distributionWorkerInfo.text =
                    if (mPurchaseOrderModel.nickname == "--") "${mPurchaseOrderModel.nicktel}" else "${mPurchaseOrderModel.nickname}  ${mPurchaseOrderModel.nicktel}"
                //预计配送时间
                distributionTimeDetail.text = mPurchaseOrderModel.nickDeliveryTime
                //预计送达时间
                arriveTimeDetail.text = mPurchaseOrderModel.nickServiceTime
                orderRecyclerView.apply {
                    adapter = CustomAdapter(mPurchaseOrderModel.orderItemList)
                    layoutManager = LinearLayoutManager(context)
                }
            }
        }

    }

    class CustomAdapter(var dataList: ArrayList<CashierGoodItemModel>) :
        RecyclerView.Adapter<PurchaseGoodItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseGoodItemHolder =
            PurchaseGoodItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cashier_order_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holderPurchase: PurchaseGoodItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holderPurchase.itemView.context).load(model.img)
                .into(holderPurchase.goodAvatar)
            holderPurchase.goodName.text = model.name
            holderPurchase.goodNum.text = "x${model.quantity}"
            if (model.oldPrice.isEmpty()) {
                holderPurchase.goodPriceOld.visibility = View.GONE
            } else {
                holderPurchase.goodPriceOld.apply {
                    visibility = View.VISIBLE
                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                    text = "￥${model.oldPrice}"
                }

            }
            holderPurchase.goodPriceNew.text = "￥${model.price}"
        }
    }

    class PurchaseGoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPriceOld: TextView = itemView.findViewById(R.id.goodPriceOld)
        var goodPriceNew: TextView = itemView.findViewById(R.id.goodPriceNew)
    }

    companion object {
        //传递订单对象的KEY
        const val ORDER_DETAIL_KEY = "ORDER_DETAIL_KEY"
    }
}