package com.example.hansungmarket.control.main.fragment.chatroom.room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.FragmentChatRoomBinding
import com.example.hansungmarket.control.utils.PagingLoadStateAdapter
import com.example.hansungmarket.control.utils.ViewBindingFragment
import com.example.hansungmarket.control.utils.addDividerDecoration
import com.example.hansungmarket.control.utils.registerObserverForScrollToTop
import com.example.hansungmarket.control.utils.setListeners
import com.example.hansungmarket.control.main.fragment.chatroom.chat.ChattingActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChatRoomFragment : ViewBindingFragment<FragmentChatRoomBinding>() {
    private val viewModel: ChatRoomViewModel by activityViewModels()
    private lateinit var launcher: ActivityResultLauncher<Intent>
    override val bf: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChatRoomBinding
        get() = FragmentChatRoomBinding::inflate
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bind()
        val adapter = ChatRoomAdapter(::clickContent)
        initrv(adapter)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) adapter.refresh()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect {
                    updateUi(it, adapter)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.bind()
    }
    private fun initrv(adapter: ChatRoomAdapter) = with(b) {
        recyclerView.adapter = adapter.withLoadStateFooter(PagingLoadStateAdapter { adapter.retry() })
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addDividerDecoration()
        loadState.emptyText.text = getString(R.string.nochat)
        loadState.setListeners(adapter, swipeRefreshLayout)
        adapter.registerObserverForScrollToTop(recyclerView)
    }
    private fun updateUi(uiState: ChatRoomUiState, adapter: ChatRoomAdapter) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.rooms)
        if (uiState.errorMessage != null) {
            viewModel.ems()
            showSnackBar(uiState.errorMessage)
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(b.root, message, Snackbar.LENGTH_SHORT).show()
    }
    private fun clickContent(chatRoomItemUiState: ChatRoomItemUiState) {
        val intent = ChattingActivity.getIntent(requireContext(), chatRoomItemUiState.id)
        launcher.launch(intent)
    }
}