package com.same.part.assistant.app.ext

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.same.part.assistant.utils.QiniuManager

/**
 * 描述　:项目中自定义类的拓展函数
 */
fun ViewPager2.init(
    fragment: Fragment,
    fragments: ArrayList<Fragment>,
    isUserInputEnabled: Boolean = true
): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?) {
    activity?.let { act ->
        val view = act.currentFocus
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}


/**
 * 隐藏软键盘
 */
fun hideSoftKeyboard(activity: Activity?,view: View?) {
    activity?.let { act ->
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
               0
            )
        }
    }
}

/**
 * 显示软键盘
 */
fun showSoftKeyboard(activity: Activity?,view: View?) {
    activity?.let { act ->
        view?.let {
            val inputMethodManager =
                act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_FORCED)
        }
    }
}

fun EditText.setCanInput(canInput: Boolean) {
    isFocusable = canInput
    isFocusableInTouchMode = canInput
    isClickable = canInput
    isCursorVisible = canInput
    if (canInput) {
        hint = ""
        requestFocus()
    }

}

/**
 * 添加图片压缩后缀地址
 */
fun ImageView.appendImageScaleSuffix(imgUrl: String, width: Int = -1, height: Int = -1): String {
    val tempWidth = if (width == -1) this.measuredWidth else width
    val tempHeight = if (height == -1) this.measuredHeight else height
    return if (imgUrl.contains(QiniuManager.IMAGE_SCALE_TYPE_SUFFIX)) imgUrl
    else imgUrl + QiniuManager.getImageScaleSuffix(
        tempWidth,
        tempHeight
    )
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) :TextWatcher{
    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
    this.addTextChangedListener(textWatcher)
    return textWatcher
}

///**
// * 防止重复点击事件 默认0.5秒内不可重复点击 跳转前做登录校验
// * @param interval 时间间隔 默认0.5秒
// * @param action 执行方法
// */
//var lastloginClickTime = 0L
//fun View.clickNoRepeatLogin(interval: Long = 500, action: (view: View) -> Unit) {
//    setOnClickListener {
//        val currentTime = System.currentTimeMillis()
//        if (lastloginClickTime != 0L && (currentTime - lastloginClickTime < interval)) {
//            return@setOnClickListener
//        }
//        lastloginClickTime = currentTime
//        if (CacheUtil.isLogin()) {
//            action(it)
//        } else {
//            //注意一下，这里我是确定我所有的拦截登录都是在MainFragment中的，所以我可以写死，但是如果不在MainFragment中时跳转，你会报错
//            nav(it).navigate(R.id.action_mainFragment_to_loginFragment)
//        }
//    }
//}
//
///**
// * 防止重复点击事件 默认0.5秒内不可重复点击 跳转前做登录校验
// * @param view 触发的view集合
// * @param interval 时间间隔 默认0.5秒
// * @param action 执行方法
// */
//fun clickNoRepeatLogin(vararg view: View?, interval: Long = 500, action: (view: View) -> Unit) {
//    view.forEach {view1 ->
//        view1?.setOnClickListener { view2 ->
//            val currentTime = System.currentTimeMillis()
//            if (lastloginClickTime != 0L && (currentTime - lastloginClickTime < interval)) {
//                return@setOnClickListener
//            }
//            lastloginClickTime = currentTime
//            if (CacheUtil.isLogin()) {
//                action(view2)
//            } else {
//                //注意一下，这里我是确定我所有的拦截登录都是在MainFragment中的，所以我可以写死，但是如果不在MainFragment中时跳转，你会报错
//                nav(view2).navigate(R.id.action_mainFragment_to_loginFragment)
//            }
//        }
//    }
//}
