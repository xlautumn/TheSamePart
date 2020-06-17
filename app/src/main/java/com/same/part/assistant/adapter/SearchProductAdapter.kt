package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.same.part.assistant.R
import com.same.part.assistant.activity.SearchActivity
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.dialog.ChooseSpecsDialogFragment
import com.same.part.assistant.manager.PurchaseProductManager
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import java.util.*

class SearchProductAdapter(
    private var mContext: SearchActivity,
    private val proxyClick: SearchActivity.ProxyClick,
    private val cartViewModel: RequestCartViewModel?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mProductModels = ArrayList<ProductDetailData>()

    fun setData(data: ArrayList<ProductDetailData>?) {
        data?.let {
            mProductModels.clear()
            mProductModels.addAll(it)
            notifyDataSetChanged()
        }
    }

    class SearchProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var goodAvatar: ImageView = view.findViewById(R.id.goodAvatar)
        var goodName: TextView = view.findViewById(R.id.goodName)
        var price: TextView = view.findViewById(R.id.tv_price)
        var goodShoppingCartRoot: LinearLayout = view.findViewById(R.id.goodShoppingCartRoot)
        var cartReduce: LinearLayout = view.findViewById(R.id.cart_reduce)
        var cartNum: TextView = view.findViewById(R.id.tv_cart_num)
        var cartIncrease: LinearLayout = view.findViewById(R.id.cart_increase)
        var chooseSpecs: TextView = view.findViewById(R.id.chooseSpecs)
        var bottomDivider: View = view.findViewById(R.id.bottomDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SearchProductViewHolder(
            LayoutInflater.from(mContext)
                .inflate(R.layout.layout_double_linked_right_order_item, parent, false)
        )

    override fun getItemCount(): Int = mProductModels.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is SearchProductViewHolder) {
            mProductModels[position].apply {
                holder.goodName.text = name ?: ""
                holder.price.text = "￥$price"
                holder.goodAvatar.takeIf { !img.isNullOrEmpty() }?.let {
                    Glide.with(mContext)
                        .load(img)
                        .into(it)
                }

                if (hasSku) {
                    holder.goodShoppingCartRoot.visibility = View.GONE
                    holder.chooseSpecs.visibility = View.VISIBLE
                    holder.chooseSpecs.setOnClickListener{
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
                    }
                } else {
                    holder.chooseSpecs.visibility = View.GONE
                    holder.goodShoppingCartRoot.visibility = View.VISIBLE
                    val cartNum = cartViewModel?.getCartNum(this) ?: 0
                    if (cartNum > 0) {
                        holder.cartIncrease.visibility = View.VISIBLE
                        holder.cartReduce.visibility = View.VISIBLE
                        holder.cartNum.apply {
                            visibility = View.VISIBLE
                            text = cartNum.toString()
                        }
                    } else {
                        holder.cartIncrease.visibility = View.VISIBLE
                        holder.cartReduce.visibility = View.GONE
                        holder.cartNum.apply {
                            visibility = View.GONE
                            text = ""
                        }
                    }
                    holder.cartIncrease.setOnClickListener {
                        proxyClick.addShopProduct(ShopProduct(this))

                    }
                    holder.cartReduce.setOnClickListener {
                        proxyClick.minusShopProduct(ShopProduct(this))
                    }
                }
            }
            holder.bottomDivider.visibility =
                if (position == mProductModels.lastIndex) View.GONE else View.VISIBLE
        }
    }
}

