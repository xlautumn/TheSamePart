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
import com.same.part.assistant.model.PurchaseGoodItemModel
import kotlinx.android.synthetic.main.activity_cashier_order_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*


/**
 * 采购订单详情
 */
class PurchaseOrderDetailActivity : AppCompatActivity() {
    private val mOrderList = arrayListOf<PurchaseGoodItemModel>().apply {
        add(
            PurchaseGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1452356606,3848535842&fm=26&gp=0.jpg",
                "超级好吃的香蕉",
                "1",
                "",
                "￥12.5"
            )
        )
        add(
            PurchaseGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1367131621,3892456581&fm=26&gp=0.jpg",
                "山东苹果五斤",
                "1",
                "",
                "￥46.0"
            )
        )
        add(
            PurchaseGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2191395536,486188374&fm=26&gp=0.jpg",
                "精品砀山梨1斤",
                "1",
                "￥17.08",
                "￥7.08"
            )
        )
        add(
            PurchaseGoodItemModel(
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=33980812,295384313&fm=26&gp=0.jpg",
                "精品8424西瓜",
                "1",
                "￥47.08",
                "￥27.08"
            )
        )
        add(
            PurchaseGoodItemModel(
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1480709382,2196250539&fm=26&gp=0.jpg",
                "精品山竹2斤",
                "1",
                "",
                "￥7.08"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_order_detail)
        //标题
        mToolbarTitle.text = "订单详情"
        //返回按钮
        mTitleBack.setOnClickListener {
            finish()
        }
        orderRecyclerView.apply {
            adapter = CustomAdapter(mOrderList)
            layoutManager = LinearLayoutManager(context)
        }
    }

    class CustomAdapter(var dataList: ArrayList<PurchaseGoodItemModel>) :
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
            Glide.with(holderPurchase.itemView.context).load(model.avatar)
                .into(holderPurchase.goodAvatar)
            holderPurchase.goodName.text = model.name
            holderPurchase.goodNum.text = "x${model.number}"
            if (model.oldPrice.isEmpty()) {
                holderPurchase.goodPriceOld.visibility = View.GONE
            } else {
                holderPurchase.goodPriceOld.apply {
                    visibility = View.VISIBLE
                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                    text = model.oldPrice
                }

            }
            holderPurchase.goodPriceNew.text = model.newPrice
        }
    }

    class PurchaseGoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPriceOld: TextView = itemView.findViewById(R.id.goodPriceOld)
        var goodPriceNew: TextView = itemView.findViewById(R.id.goodPriceNew)
    }
}