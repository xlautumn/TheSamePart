package com.same.part.assistant.activity

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
import com.same.part.assistant.model.GoodItemModel
import kotlinx.android.synthetic.main.activity_cashier_order_detail.*
import kotlinx.android.synthetic.main.toolbar_title.*


/**
 * 采购订单详情
 */
class PurchaseOrderDetailActivity : AppCompatActivity() {
    /**
     * 当前详情页的样式
     */
    private var mCurrentStyle = STYLE_CASHIER_DETAIL
    private val mOrderList = arrayListOf<GoodItemModel>().apply {
        add(
            GoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1452356606,3848535842&fm=26&gp=0.jpg",
                "超级好吃的香蕉",
                "1",
                "￥12.5"
            )
        )
        add(
            GoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1367131621,3892456581&fm=26&gp=0.jpg",
                "山东苹果五斤",
                "1",
                "￥46.0"
            )
        )
        add(
            GoodItemModel(
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2191395536,486188374&fm=26&gp=0.jpg",
                "精品砀山梨1斤",
                "1",
                "￥7.08"
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

    class CustomAdapter(var dataList: ArrayList<GoodItemModel>) :
        RecyclerView.Adapter<GoodItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodItemHolder =
            GoodItemHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.order_good_item,
                    parent,
                    false
                )
            )


        override fun getItemCount(): Int = dataList.size


        override fun onBindViewHolder(holder: GoodItemHolder, position: Int) {
            val model = dataList[position]
            Glide.with(holder.itemView.context).load(model.avatar).into(holder.goodAvatar)
            holder.goodName.text = model.name
            holder.goodNum.text = "x${model.number}"
            holder.goodPrice.text = model.price
        }
    }

    class GoodItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var goodAvatar: ImageView = itemView.findViewById(R.id.goodAvatar)
        var goodName: TextView = itemView.findViewById(R.id.goodName)
        var goodNum: TextView = itemView.findViewById(R.id.goodNum)
        var goodPrice: TextView = itemView.findViewById(R.id.goodPrice)
    }

    companion object {
        //Intent的Key值
        const val STYLE_DETAIL_KEY = "STYLE_DETAIL_KEY"
        //收银详情样式
        const val STYLE_CASHIER_DETAIL = 0
        //采购详情样式
        const val STYLE_PURCHASE_DETAIL = 1

    }
}