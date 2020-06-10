package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.dialog.ChooseSpecsDialogFragment
import com.same.part.assistant.fragment.PurchaseFragment
import com.same.part.assistant.manager.PurchaseProductManager
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import java.util.*

class PurchaseProductAdapter(private var mContext: FragmentActivity,private val proxyClick: PurchaseFragment.ProxyClick,private val cartViewModel:RequestCartViewModel) :
    BaseAdapter() {

    private var mProductModels = ArrayList<ProductDetailData>()

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
        mProductModels[position].apply {
            goodViewHolder.goodName?.text = name ?: ""
            goodViewHolder.price?.text = "￥$price"
            goodViewHolder.goodAvatar?.takeIf { !img.isNullOrEmpty() }?.let {
                Glide.with(mContext)
                    .load(img)
                    .into(it)
            }

            if (hasSku) {
                goodViewHolder.goodShoppingCartRoot?.visibility = View.GONE
                goodViewHolder.chooseSpecs?.visibility = View.VISIBLE
                goodViewHolder.chooseSpecs?.setOnClickListener(View.OnClickListener {
                    //规格查询
                    PurchaseProductManager.INSTANCE.getProductSpecs(
                        productId,
                        { propertyList, propertyPriceList ->
                            propertyList?.takeIf { list -> list.isNotEmpty() }?.let {
                                val dialogFragment =
                                    ChooseSpecsDialogFragment.create(name ?: "", mContext)
                                dialogFragment.setData(it, propertyPriceList)
                                dialogFragment.setListener { productSku ->
                                    //加入购物车的请求
                                    proxyClick.addShopProduct(
                                        ShopProduct(
                                            this,
                                            productSkuNumber = productSku.number
                                        )
                                    )
                                }
                                dialogFragment.showDialog(mContext.supportFragmentManager)
                            }
                        }, onError = {
                            ToastUtils.showShort(it)
                        })
                })
            } else {
                goodViewHolder.chooseSpecs?.visibility = View.GONE
                goodViewHolder.goodShoppingCartRoot?.visibility = View.VISIBLE
                val cartNum = cartViewModel.getCartNum(this)
                if (cartNum > 0) {
                    goodViewHolder.cartIncrease?.visibility = View.VISIBLE
                    goodViewHolder.cartReduce?.visibility = View.VISIBLE
                    goodViewHolder.cartNum?.apply {
                        visibility = View.VISIBLE
                        text = cartNum.toString()
                    }
                } else {
                    goodViewHolder.cartIncrease?.visibility = View.VISIBLE
                    goodViewHolder.cartReduce?.visibility = View.GONE
                    goodViewHolder.cartNum?.apply {
                        visibility = View.GONE
                        text = ""
                    }
                }
                goodViewHolder.cartIncrease?.setOnClickListener(View.OnClickListener {
                    proxyClick.addShopProduct(ShopProduct(this))

                })
                goodViewHolder.cartReduce?.setOnClickListener(View.OnClickListener {
                    proxyClick.minusShopProduct(ShopProduct(this))
                })
            }
        }
        goodViewHolder.bottomDivider?.visibility =
            if (position == mProductModels.lastIndex) View.GONE else View.VISIBLE
        return view
    }

    fun setData(data: ArrayList<ProductDetailData>?) {
        data?.let {
            mProductModels.clear()
            mProductModels.addAll(it)
            notifyDataSetChanged()
        }
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
        var bottomDivider: View? = null
    }
}

