package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.same.part.assistant.data.model.CartProduct
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.databinding.CartProductItemBinding
import com.same.part.assistant.fragment.PurchaseFragment

class CartProductAdapter(private val proxyClick: PurchaseFragment.ProxyClick) : RecyclerView.Adapter<CartProductViewHolder>() {

    private val data: ArrayList<CartProduct> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            proxyClick
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun setData(data: ArrayList<CartProduct>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}

class CartProductViewHolder(private val binding: CartProductItemBinding,private val proxyClick: PurchaseFragment.ProxyClick) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(cartProduct: CartProduct) {
        cartProduct.shopProduct.productDetailData.apply {
            binding.goodAvatar.takeIf { !img.isNullOrEmpty() }?.let {
                Glide.with(binding.root.context)
                    .load(img)
                    .into(it)
            }

            binding.goodName.text = this.name
            binding.tvPrice.text = "￥${cartProduct.price}"
            binding.tvCartNum.text = cartProduct.shopProduct.num.toString()
            binding.goodTag.text = cartProduct.getProperties().joinToString(separator = "/")
            binding.cartIncrease.setOnClickListener(View.OnClickListener {
                proxyClick.addShopProduct(ShopProduct(this,1,cartProduct.shopProduct.productSkuNumber,cartProduct.shopProduct.properties))

            })
            binding.cartReduce.setOnClickListener(View.OnClickListener {
                proxyClick.minusShopProduct(ShopProduct(this,productSkuNumber = cartProduct.shopProduct.productSkuNumber))
            })
        }
    }

}