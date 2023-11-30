package com.example.hansungmarket.control.main.fragment.chatroom.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hansungmarket.databinding.ActivityChattingBinding
import kotlinx.coroutines.launch

class ChattingActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, roomId: String): Intent {
            return Intent(context, ChattingActivity::class.java).apply {
                putExtra("roomUuid", roomId)
            }
        }
    }
    private val viewModel: ChattingViewModel by viewModels()
    private lateinit var binding: ActivityChattingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roomId = intent.getStringExtra("roomUuid")
        viewModel.bind(roomUuid = requireNotNull(roomId))
        initE()
        val adapter = ChattingAdapter()
        init(adapter)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect {
                    upd(it, adapter)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val roomUuid = intent.getStringExtra("roomUuid")
        viewModel.bind(roomUuid = requireNotNull(roomUuid))
    }
    private fun initE() = with(binding) {
        sendButton.setOnClickListener {
            viewModel.sendMessage()
            editChatMessage.setText("")
            recyclerView.layoutManager = LinearLayoutManager(this@ChattingActivity).apply {
                this.stackFromEnd = true
            }
        }
        editChatMessage.addTextChangedListener {
            if (it != null) viewModel.updC(it.toString())
        }
    }
    private fun init(adapter: ChattingAdapter) = with(binding) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@ChattingActivity).apply {
            this.stackFromEnd = true
        }
    }
    private fun upd(uiState: ChattingUiState, adapter: ChattingAdapter) = with(binding) {
        adapter.submitList(uiState.chats)
        if (uiState.chats != null)
            emptyText.isVisible = uiState.chats.isEmpty()
        if (uiState.curChatuis != null)
            otherUserName.text = uiState.curChatuis.userName
        progressBar.isVisible = uiState.loading
    }
}
