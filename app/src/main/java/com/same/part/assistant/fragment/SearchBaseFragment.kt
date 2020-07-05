package com.same.part.assistant.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.R
import com.same.part.assistant.adapter.SearchHistoryAdapter
import com.same.part.assistant.app.base.BaseFragment
import com.same.part.assistant.app.ext.hideSoftKeyboard
import com.same.part.assistant.app.ext.showSoftKeyboard
import com.same.part.assistant.databinding.LayoutSearchBaseFragmentBinding
import com.same.part.assistant.utils.SharedPreferenceUtil
import com.same.part.assistant.view.CustomerLayoutManager
import com.same.part.assistant.viewmodel.state.SearchBaseViewModel
import kotlinx.android.synthetic.main.toolbar_title.*

/**
 * 搜索页面基类
 */
abstract class SearchBaseFragment<VM:SearchBaseViewModel<E>,E>:BaseFragment<VM,LayoutSearchBaseFragmentBinding>() ,
    TextWatcher,
    View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private lateinit var mPreferInstance: SharedPreferenceUtil

    override fun layoutId(): Int = R.layout.layout_search_base_fragment

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = SearchProxyClick(this)

        mToolbarTitle.text = "搜索"
        mTitleBack.setOnClickListener {
            goBack()
        }
        mDatabind.searchHistory.layoutManager = CustomerLayoutManager(0)
        val historyAdapter = SearchHistoryAdapter()
        historyAdapter.setItemOnClickListener {
            mDatabind.etSearch.clearFocus()
            mDatabind.etSearch.setText(it)
            mViewModel.showHistory.postValue(false)
            mViewModel.requestSearchResultList(it)
        }
        mDatabind.searchHistory.adapter = historyAdapter
        subscribeHistoryUi(historyAdapter)
        mDatabind.etSearch.requestFocus()
        mDatabind.etSearch.addTextChangedListener(this)
        mDatabind.etSearch.onFocusChangeListener = this
        mDatabind.etSearch.setOnEditorActionListener(this)

        mPreferInstance = SharedPreferenceUtil.getInstance(requireContext())
        mViewModel.getHistoryData(mPreferInstance)
    }

    override fun lazyLoadData() {
        initRecyclerView(mDatabind.searchResult)
        subscribeSearchResultUi()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.let { chars ->
            mViewModel.showClear.postValue(chars.isNotEmpty())
            chars.takeIf { it.isEmpty() }?.apply {
                mViewModel.showResult.postValue(false)
                mViewModel.showHistory.postValue(mViewModel.searchHistoryData.value?.isNotEmpty())
                mViewModel.clear()
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        mDatabind.etSearch.text?.takeIf { it.isNotEmpty() }?.let {
            mViewModel.showClear.postValue(true)
        }
        if (!hasFocus) {
            activity?.let {hideSoftKeyboard(it, v)  }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == InputType.TYPE_NULL) {
            v?.let { textView ->
                textView.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                    mViewModel.refreshHistoryData(it)
                    mViewModel.requestSearchResultList(it)
                }
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        mViewModel.updateHistoryCache(mPreferInstance)
    }
    abstract fun initRecyclerView(recyclerView: RecyclerView)

    abstract fun updateRecyclerView(data:List<E>)

    open fun goBack(){
        findNavController().navigateUp()
    }

    private fun subscribeSearchResultUi() {
        mViewModel.searchResultList.observe(this, Observer {
            val search = mDatabind.etSearch.text?.toString() ?: ""
            mViewModel.showResult.postValue(search.isNotEmpty())
            mViewModel.hasSearchResult.postValue(it.size > 0)
            updateRecyclerView(it)
        })
    }

    /**
     * 刷新历史记录UI
     */
    private fun subscribeHistoryUi(adapter: SearchHistoryAdapter) {
        mViewModel.searchHistoryData.observe(this, Observer {
            val search = mDatabind.etSearch.text?.toString() ?: ""
            mViewModel.showHistory.postValue(
                search.isEmpty() && (mViewModel.searchHistoryData.value?.isNotEmpty() ?: false)
            )
            adapter.setData(it)
        })
    }

}

 class SearchProxyClick<VM:SearchBaseViewModel<E>,E>(private val fragment: SearchBaseFragment<VM,E>) {
    /**
     * 右上角取消按钮
     */
    fun cancel() {
        fragment.goBack()
    }

    /**
     * 删除历史记录
     */
    fun delete() {
        fragment.mViewModel.clearHistory()
    }

    /**
     * 删除搜索内容
     */
    fun clear() {
        fragment.mViewModel.showResult.postValue(false)
        fragment.mDatabind.etSearch.text = null
        fragment.mViewModel.clear()
        fragment.mDatabind.etSearch.requestFocus()
        fragment.mViewModel.showHistory.postValue(fragment.mViewModel.searchHistoryData.value?.isNotEmpty())
        fragment.activity?.let {
            showSoftKeyboard(it, fragment.mDatabind.etSearch)
        }

    }
}