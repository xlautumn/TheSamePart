package com.same.part.assistant.adapter

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isVisible")
fun bindIsVisible(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}