package com.same.part.assistant.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.same.part.assistant.R
import com.same.part.assistant.adapter.SearchHistoryAdapter
import com.same.part.assistant.adapter.SearchProductAdapter
import com.same.part.assistant.app.base.BaseActivity
import com.same.part.assistant.app.ext.hideSoftKeyboard
import com.same.part.assistant.app.ext.showSoftKeyboard
import com.same.part.assistant.data.model.ShopProduct
import com.same.part.assistant.databinding.ActivitySearchBinding
import com.same.part.assistant.utils.SharedPreferenceUtil
import com.same.part.assistant.view.CustomerLayoutManager
import com.same.part.assistant.viewmodel.request.RequestCartViewModel
import com.same.part.assistant.viewmodel.request.RequestSearchResultViewModel
import com.same.part.assistant.viewmodel.state.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.toolbar_title.*
import me.hgj.jetpackmvvm.ext.getAppViewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>(), TextWatcher,
    View.OnFocusChangeListener, TextView.OnEditorActionListener {

    private val mSearchResultViewModel: RequestSearchResultViewModel by lazy {
        getAppViewModel<RequestSearchResultViewModel>()
    }

    private val requestCartViewModel: RequestCartViewModel by lazy { getAppViewModel<RequestCartViewModel>() }
    private lateinit var mPreferInstance: SharedPreferenceUtil

    override fun layoutId(): Int = R.layout.activity_search

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()

        mToolbarTitle.text = "搜索"
        mTitleBack.setOnClickListener {
            finish()
        }

        mDatabind.searchHistory.layoutManager = CustomerLayoutManager(0)
        val historyAdapter = SearchHistoryAdapter()
        historyAdapter.setItemOnClickListener {
            mDatabind.etSearch.clearFocus()
            mDatabind.etSearch.setText(it)
            mViewModel.showHistory.postValue(false)
            mSearchResultViewModel.requestSearchResultList(it)
        }
        mDatabind.searchHistory.adapter = historyAdapter
        subscribeHistoryUi(historyAdapter)

        val searchResultAdapter = SearchProductAdapter(this, ProxyClick(), requestCartViewModel)
        mDatabind.searchResult.layoutManager = LinearLayoutManager(this)
        mDatabind.searchResult.adapter = searchResultAdapter
        subscribeSearchResultUi(searchResultAdapter)

        mDatabind.etSearch.requestFocus()
        mDatabind.etSearch.addTextChangedListener(this)
        mDatabind.etSearch.onFocusChangeListener = this
        mDatabind.etSearch.setOnEditorActionListener(this)

        mPreferInstance = SharedPreferenceUtil.getInstance(this)
        mViewModel.getHistoryData(mPreferInstance)

        requestCartViewModel.cartProductList.observe(this, Observer {
            searchResultAdapter?.notifyDataSetChanged()
        })
    }

    private fun subscribeSearchResultUi(adapter: SearchProductAdapter) {
        mSearchResultViewModel.searchResultList.observe(this, Observer {
            val search = etSearch?.text?.toString() ?: ""
            mViewModel.showResult.postValue(search.isNotEmpty())
            mViewModel.hasSearchResult.postValue(it.size > 0)
            adapter.setData(it)
        })
    }

    /**
     * 刷新历史记录UI
     */
    private fun subscribeHistoryUi(adapter: SearchHistoryAdapter) {
        mViewModel.searchHistoryData.observe(this, Observer {
            val search = etSearch?.text?.toString() ?: ""
            mViewModel.showHistory.postValue(
                search.isEmpty() && (mViewModel.searchHistoryData.value?.isNotEmpty() ?: false)
            )
            adapter.setData(it)
        })
    }

    override fun onPause() {
        super.onPause()
        mViewModel.updateHistoryCache(mPreferInstance)
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
                mSearchResultViewModel.clear()
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        mDatabind.etSearch.text?.takeIf { it.isNotEmpty() }?.let {
            mViewModel.showClear.postValue(true)
        }
        if (!hasFocus) {
            hideSoftKeyboard(this, v)
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == InputType.TYPE_NULL) {
            v?.let { textView ->
                textView.text?.toString()?.takeIf { it.isNotEmpty() }?.let {
                    mViewModel.refreshHistoryData(it)
                    mSearchResultViewModel.requestSearchResultList(it)
                }
            }
        }
        return false
    }


    inner class ProxyClick {
        /**
         * 右上角取消按钮
         */
        fun cancel() {
            finish()
        }

        /**
         * 删除历史记录
         */
        fun delete() {
            mViewModel.clearHistory()
        }

        /**
         * 删除搜索内容
         */
        fun clear() {
            mViewModel.showResult.postValue(false)
            mDatabind.etSearch.text = null
            mSearchResultViewModel.clear()
            mDatabind.etSearch.requestFocus()
            mViewModel.showHistory.postValue(mViewModel.searchHistoryData.value?.isNotEmpty())
            showSoftKeyboard(this@SearchActivity, mDatabind.etSearch)
        }

        /**
         * 添加购物车
         */
        fun addShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.addShopProduct(shopProduct)
        }

        /**
         * 减少购物车数量
         */
        fun minusShopProduct(shopProduct: ShopProduct) {
            requestCartViewModel.minusShopProduct(shopProduct)
        }
    }
}