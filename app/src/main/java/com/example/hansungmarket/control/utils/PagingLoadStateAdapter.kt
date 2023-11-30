package com.example.hansungmarket.control.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ItemLoadStateBinding

class PagingLoadStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<PagingLoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, loadState: LoadState
    ): PagingLoadStateViewHolder = PagingLoadStateViewHolder(parent, retry)
    override fun onBindViewHolder(holder: PagingLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}
class PagingLoadStateViewHolder(parent: ViewGroup, retry: () -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_load_state, parent, false)
) {
    private val b = ItemLoadStateBinding.bind(itemView)
    private val pb: ProgressBar = b.progressBar
    private val e: TextView = b.errorMsg
    private val r: Button = b.retryButton.also{it.setOnClickListener { retry() }}
    fun bind(loadState: LoadState) {
        pb.isVisible = loadState is LoadState.Loading
        r.isVisible = loadState is LoadState.Error
        e.isVisible = loadState is LoadState.Error
    }
}
