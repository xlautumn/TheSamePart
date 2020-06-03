package com.same.part.assistant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.data.model.CartProduct
import com.same.part.assistant.databinding.CartProductItemBinding

class CartProductAdapter : RecyclerView.Adapter<CartProductViewHolder>() {

    private val data: ArrayList<CartProduct> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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

class CartProductViewHolder(private val binding: CartProductItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(cartProduct: CartProduct) {
        binding.tvProductName.text = cartProduct.shopProduct.productDetailData.name
    }

}