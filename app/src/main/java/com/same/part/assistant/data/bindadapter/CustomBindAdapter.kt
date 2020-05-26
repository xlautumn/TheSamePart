package com.same.part.assistant.data.bindadapter
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.same.part.assistant.R
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 描述　: 自定义 BindingAdapter
 */
object CustomBindAdapter {

    @BindingAdapter(value = ["circleImageUrl"])
    @JvmStatic
    fun imageUrl(view: CircleImageView, url: String) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .placeholder(R.drawable.icn_head_img)
            .error(R.drawable.icn_head_img)
            .into(view)
    }







}