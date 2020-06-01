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
import com.same.part.assistant.data.model.CashierOrderModel
import kotlinx.android.synthetic.main.activity_cashier_order_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.util.copyToClipboard


/**
 * 订单详情
 */
class CashierOrderDetailActivity : AppCompatActivity() {
    //订单对象类
    private lateinit var mCashierOrderModel: CashierOrderModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier_order_detail)
        //标题
        mToolbarTitle.text = "订单详情"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        //获取数据
        intent?.getSerializableExtra(ORDER_DETAIL_KEY)?.apply {
            if (this is CashierOrderModel) {
                mCashierOrderModel = this
                //订单号
                orderNo.text = mCashierOrderModel.no
                //复制
                copyOrderNo.setOnClickListener {
                    copyToClipboard(
                        this@CashierOrderDetailActivity,
                        mCashierOrderModel.no,
                        needShowToast = true,
                        toastMsg = "订单编号已复制"
                    )
                }
                //支付方式
                payMethod.text = mCashierOrderModel.payment
                //订单时间
                orderTime.text = mCashierOrderModel.addTime
                //商品详情
                orderRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = CustomAdapter(mCashierOrderModel.orderItemList)
                }
                //店铺优惠券
                shopCouponMinus.text = "-￥${mCashierOrderModel.shopCouponPrice}"
                //平台优惠券
                platformCouponMinus.text = "-￥${mCashierOrderModel.platformCouponPrice}"
                //会员卡折扣（暂无）

                //合计
                orderAmount.text = "￥${mCashierOrderModel.price}"
            }
        }

    }

    inner class CustomAdapter(var dataList: ArrayList<CashierGoodItemModel>) :
        RecyclerView.Adapter<CashierGoodItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashierGoodItemHolder =
            CashierGoodItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.cashier_order_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holderCashier: CashierGoodItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holderCashier.itemView.context).load(model.img)
                .into(holderCashier.goodAvatar)
            holderCashier.goodName.text = model.name
            holderCashier.goodNum.text = "x${model.quantity}"
            if (model.oldPrice.isEmpty()) {
                holderCashier.goodPriceOld.visibility = View.GONE
            } else {
                holderCashier.goodPriceOld.apply {
                    visibility = View.VISIBLE
                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                    text = model.oldPrice
                }

            }
            holderCashier.goodPriceNew.text = model.price
        }
    }

    class CashierGoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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