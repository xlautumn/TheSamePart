package com.same.part.assistant.app.ext

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

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
