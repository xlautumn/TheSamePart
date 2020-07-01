package com.same.part.assistant.fragment

import androidx.recyclerview.widget.RecyclerView
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.viewmodel.request.RequestSearchCustomViewModel

/**
 * 搜索客户
 */
class SearchCustomFragment : SearchBaseFragment<RequestSearchCustomViewModel,ProductDetailData>() {

    override fun initRecyclerView(recyclerView: RecyclerView) {
    }

    override fun updateRecyclerView(data: List<ProductDetailData>) {
    }
}