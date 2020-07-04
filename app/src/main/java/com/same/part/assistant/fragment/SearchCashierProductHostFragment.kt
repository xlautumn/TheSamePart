package com.same.part.assistant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.same.part.assistant.R
import com.same.part.assistant.databinding.FragmentSearchCashierProductHostBinding
import com.same.part.assistant.viewmodel.state.SuitableProductViewModel
import me.hgj.jetpackmvvm.ext.getViewModel

class SearchCashierProductHostFragment : Fragment() {
    private lateinit var binding: FragmentSearchCashierProductHostBinding
    private lateinit var suitableProductViewModel: SuitableProductViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchCashierProductHostBinding.inflate(inflater, container, false)
        binding.tvConfirm.setOnClickListener {
            val fragment = childFragmentManager.findFragmentById(R.id.search_cashier_product_fragment)
            if (fragment is SearchCashierProductFragment){
                fragment.getAdapter().data.filter { !it.hasAdd && it.isSelect }.map { it.product }.let {
                    suitableProductViewModel.addProductList(it)
                }
            }
            findNavController().navigate(R.id.action_searchCashierProductHostFragment_to_suitableProductFragment)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.takeIf { it is AppCompatActivity }?.let { it as AppCompatActivity }?.apply {
            suitableProductViewModel = getViewModel<SuitableProductViewModel>()
        }
    }
}