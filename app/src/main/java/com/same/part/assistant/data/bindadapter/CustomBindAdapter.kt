package com.same.part.assistant.data.bindadapter

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
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
    fun imageUrl(view: ImageView, url: String) {
        Glide.with(view.context.applicationContext)
            .load(url)
            .placeholder(R.drawable.home_user_avatar)
            .error(R.drawable.home_user_avatar)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.setImageDrawable(resource)
                }
            })
    }

    @BindingAdapter(value = ["thresholdNum"])
    @JvmStatic
    fun thresholdNum(textView: TextView, num: String) {
        textView.text = if (num == "0") {
            "无使用门槛"
        } else {
            "满${num}元使用"
        }
    }

    @BindingAdapter(value = ["receiveWay"])
    @JvmStatic
    fun receiveWay(textView: TextView, num: Int) {
        textView.text = when (num) {
            1 -> "直接领取"
            2 -> "付费领取"
            else -> "满足条件自动领取"
        }
    }


    @BindingAdapter(value = ["switchButton"])
    @JvmStatic
    fun switchButton(switchCompat: SwitchCompat, num: Int) {
        switchCompat.isChecked = num == 1
    }

}