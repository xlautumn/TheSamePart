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
import kotlinx.android.synthetic.main.activity_cashier_order_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*


/**
 * 订单详情
 */
class CashierOrderDetailActivity : AppCompatActivity() {
    private val mOrderList = arrayListOf<CashierGoodItemModel>().apply {
        add(
            CashierGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1452356606,3848535842&fm=26&gp=0.jpg",
                "超级好吃的香蕉",
                "1",
                "￥12.5",
                "￥2.5"
            )
        )
        add(
            CashierGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1367131621,3892456581&fm=26&gp=0.jpg",
                "山东苹果五斤",
                "1",
                "",
                "￥46.0"
            )
        )
        add(
            CashierGoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2191395536,486188374&fm=26&gp=0.jpg",
                "精品砀山梨1斤",
                "1",
                "",
                "￥46.0"
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier_order_detail)
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

    class CustomAdapter(var dataList: ArrayList<CashierGoodItemModel>) :
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
            Glide.with(holderCashier.itemView.context).load(model.avatar)
                .into(holderCashier.goodAvatar)
            holderCashier.goodName.text = model.name
            holderCashier.goodNum.text = "x${model.number}"
            if (model.oldPrice.isEmpty()) {
                holderCashier.goodPriceOld.visibility = View.GONE
            } else {
                holderCashier.goodPriceOld.apply {
                    visibility = View.VISIBLE
                    paint.flags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                    text = model.oldPrice
                }

            }
            holderCashier.goodPriceNew.text = model.newPrice
        }
    }

    class CashierGoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPriceOld: TextView = itemView.findViewById(R.id.goodPriceOld)
        var goodPriceNew: TextView = itemView.findViewById(R.id.goodPriceNew)
    }
}