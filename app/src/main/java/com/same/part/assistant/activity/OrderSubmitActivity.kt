package com.same.part.assistant.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.same.part.assistant.R
import com.same.part.assistant.adapter.CustomOrderAdapter
import com.same.part.assistant.model.GoodItemModel
import kotlinx.android.synthetic.main.activity_order_submit.*
import kotlinx.android.synthetic.main.toolbar_title.*

class OrderSubmitActivity : AppCompatActivity(), View.OnClickListener {

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
        setContentView(R.layout.activity_order_submit)
        mToolbarTitle.text = "提交订单"
        mTitleBack.setOnClickListener {
            finish()
        }
        //地址信息
        address.text = "无锡市惠山区绿地世纪华惠路89号"
        shopName.text = "生鲜店"
        customerInfo.text = "李明炎 17796965656"
        //订单详情页
        orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CustomOrderAdapter(mOrderList)
        }
        rlPayContain.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rlPayContain -> {
                //TODO 点击支付方式。弹出选择

            }
        }
    }
}