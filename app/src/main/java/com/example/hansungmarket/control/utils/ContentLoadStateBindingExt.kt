package com.example.hansungmarket.control.utils

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ContentLoadStateBinding
import java.net.ConnectException

fun <PA : PagingDataAdapter<T, VH>, T, VH> ContentLoadStateBinding.setListeners(
    adapter: PA, swipeToRefresh: SwipeRefreshLayout) {
    swipeToRefresh.setOnRefreshListener { adapter.refresh() }
    this.retryButton.setOnClickListener {
        adapter.retry()
    }
    adapter.addLoadStateListener { loadStates ->
        val refreshLoadState = loadStates.refresh
        val isE = refreshLoadState is LoadState.Error
        val isEmptyTxt = refreshLoadState is LoadState.NotLoading && adapter.getItemCount() < 1
        emptyText.isVisible = isEmptyTxt
        swipeToRefresh.isRefreshing = refreshLoadState is LoadState.Loading
        retryButton.isVisible = isE
        errorMsg.isVisible = isE
        if (refreshLoadState is LoadState.Error) {
            errorMsg.text = when (val exception = refreshLoadState.error) {
                is ConnectException -> root.context.getString(R.string.failc)
                else -> exception.message
            }
        }
    }
}

fun <T : Any, VH : RecyclerView.ViewHolder> PagingDataAdapter<T, VH>.registerObserverForScrollToTop(
    recyclerView: RecyclerView, insertedFirst: Boolean = true, rangeMoved: Boolean = true
) {
    this.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (positionStart == 0 && insertedFirst) {
                recyclerView.scrollToPosition(0)
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            if (rangeMoved) recyclerView.scrollToPosition(0)
        }
    })
}
