package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.same.part.assistant.R
import com.same.part.assistant.dialog.ChooseSpecsDialogFragment
import com.same.part.assistant.model.GoodClassModel
import java.util.ArrayList

class PurchaseProductAdapter(private var mContext: FragmentActivity) :
    BaseAdapter() {

    private var mProductModels = ArrayList<GoodClassModel.GoodModel>()

    override fun getCount(): Int = mProductModels.size

    override fun getItem(position: Int): Any? = mProductModels?.get(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var view: View
        var goodViewHolder: GoodViewHolder
        if (convertView == null || convertView.tag == null
            || convertView.tag !is GoodViewHolder
        ) {
            view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_double_linked_right_order_item, null)
            goodViewHolder = GoodViewHolder()
            goodViewHolder.apply {
                goodAvatar = view.findViewById(R.id.goodAvatar)
                goodName = view.findViewById(R.id.goodName)
                goodNameExplain = view.findViewById(R.id.goodNameExplain)
                goodTag = view.findViewById(R.id.goodTag)
                price = view.findViewById(R.id.tv_price)
                subPrice = view.findViewById(R.id.tv_sub_price)
                subTag = view.findViewById(R.id.tv_sub_tag)
                goodShoppingCartRoot = view.findViewById(R.id.goodShoppingCartRoot)
                cartReduce = view.findViewById(R.id.cart_reduce)
                cartNum = view.findViewById(R.id.tv_cart_num)
                cartIncrease = view.findViewById(R.id.cart_increase)
                chooseSpecs = view.findViewById(R.id.chooseSpecs)
            }
            view.tag = goodViewHolder
        } else {
            view = convertView
            goodViewHolder = view.tag as GoodViewHolder
        }
        //填充View
        mProductModels?.get(position)?.apply {
            goodViewHolder.goodName?.text = name ?: ""
            goodViewHolder.goodNameExplain?.text = desc ?: ""
            goodViewHolder.price?.text = price ?: ""

            if (inCart) {
                goodViewHolder.chooseSpecs?.visibility = View.GONE
                goodViewHolder.goodShoppingCartRoot?.visibility = View.VISIBLE
                goodViewHolder.cartIncrease?.setOnClickListener(View.OnClickListener {
                    //TODO 购买数量 增加

                })
                goodViewHolder.cartReduce?.setOnClickListener(View.OnClickListener {
                    //TODO 购买数量减少

                })
            } else {
                goodViewHolder.goodShoppingCartRoot?.visibility = View.GONE
                goodViewHolder.chooseSpecs?.visibility = View.VISIBLE
                goodViewHolder.chooseSpecs?.setOnClickListener(View.OnClickListener {
                    val dialogFragment = ChooseSpecsDialogFragment()
                    dialogFragment.showDialog(mContext.supportFragmentManager)
                })
            }
        }
        return view
    }

    fun setData(data: ArrayList<GoodClassModel.GoodModel>) {
        mProductModels.clear()
        mProductModels.addAll(data)
    }

    class GoodViewHolder {
        var goodAvatar: ImageView? = null
        var goodName: TextView? = null
        var goodNameExplain: TextView? = null
        var goodTag: TextView? = null
        var price: TextView? = null
        var subPrice: TextView? = null
        var subTag: TextView? = null
        var goodShoppingCartRoot: LinearLayout? = null
        var cartNum: TextView? = null
        var cartReduce: LinearLayout? = null
        var cartIncrease: LinearLayout? = null
        var chooseSpecs: TextView? = null
    }
}

