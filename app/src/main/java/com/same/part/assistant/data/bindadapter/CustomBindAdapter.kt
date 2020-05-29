package com.same.part.assistant.data.bindadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
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
            .placeholder(R.drawable.home_user_avatar)
            .error(R.drawable.home_user_avatar)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.setImageDrawable(resource)
                }
            })
    }


}