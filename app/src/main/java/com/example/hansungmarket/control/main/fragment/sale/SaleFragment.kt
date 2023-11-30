package com.example.hansungmarket.control.main.fragment.sale

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.FragmentSaleBinding
import com.example.hansungmarket.control.utils.PagingLoadStateAdapter
import com.example.hansungmarket.control.utils.ViewBindingFragment
import com.example.hansungmarket.control.utils.addDividerDecoration
import com.example.hansungmarket.control.utils.registerObserverForScrollToTop
import com.example.hansungmarket.control.utils.setListeners
import com.example.hansungmarket.control.main.fragment.sale.detail.SaleDetailActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SaleFragment : ViewBindingFragment<FragmentSaleBinding>() {
    private val viewModel: SaleViewModel by activityViewModels()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    override val bf: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSaleBinding
        get() = FragmentSaleBinding::inflate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = SaleAdapter(onClickSaleItem = ::clickItem)
        viewModel.bind()
        initR(adapter)
        initE()
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                adapter.refresh()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect {
                    updUi(it, adapter)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.bind()
    }
    private fun initE() = with(b) {
        spinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.saleI, android.R.layout.simple_spinner_item
        )
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.updateSortType(spinner.getItemAtPosition(position).toString())
                viewModel.bind()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        checkbox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.canUpd(isChecked)
            viewModel.bind()
        }
    }
    private fun initR(adapter: SaleAdapter) = with(b) {
        recyclerView.adapter =
            adapter.withLoadStateFooter(PagingLoadStateAdapter { adapter.retry() })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addDividerDecoration()
        loadState.setListeners(adapter, swipeRefreshLayout)
        adapter.registerObserverForScrollToTop(recyclerView)
    }
    private fun updUi(uis: SaleUiState, adapter: SaleAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uis.saleP)
        if (uis.errorMessage != null) {
            viewModel.errorMessageShown()
            showSnackBar(uis.errorMessage)
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(b.root, message, Snackbar.LENGTH_SHORT).show()
    }
    private fun clickItem(saleItemUiState: SaleItemUiState) {
        val intent = SaleDetailActivity.getIntent(requireContext(), saleItemUiState.id)
        launcher.launch(intent)
    }
}