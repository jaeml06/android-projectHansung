package com.example.hansungmarket.control.main.fragment.chatroom.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ItemMyChatMessageBinding
import com.example.hansungmarket.databinding.ItemOtherChatMessageBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChattingAdapter : ListAdapter<ChatItemUiState, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == MY_CHAT) {
            val binding = ItemMyChatMessageBinding.inflate(layoutInflater, parent, false)
            MyChatItemViewHolder(binding)
        } else {
            val binding = ItemOtherChatMessageBinding.inflate(layoutInflater, parent, false)
            OtherChatItemViewHolder(binding)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MY_CHAT)
            (holder as MyChatItemViewHolder).bind(currentList[position])
        else
            (holder as OtherChatItemViewHolder).bind(currentList[position])
    }
    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].M) MY_CHAT
        else OTHER_CHAT
    }
    override fun submitList(list: MutableList<ChatItemUiState>?) {
        super.submitList(list)
    }
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatItemUiState>() {
            override fun areItemsTheSame(
                oldItem: ChatItemUiState, newItem: ChatItemUiState
            ): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(
                oldItem: ChatItemUiState, newItem: ChatItemUiState
            ): Boolean {
                return oldItem == newItem
            }
        }
        private const val MY_CHAT = 1
        private const val OTHER_CHAT = 2
    }
    inner class MyChatItemViewHolder(private val binding: ItemMyChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val storageReference = Firebase.storage.reference
        fun bind(uiState: ChatItemUiState) = with(binding) {
            textChatMessageMe.text = uiState.content
            myUserDate.text = uiState.timestamp
            val glide = Glide.with(root)
            val ref = uiState.profileImg?.let { storageReference.child(it) }
            ref?.downloadUrl?.addOnSuccessListener { uri ->
                glide.load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fallback(R.drawable.baseline_person_24).circleCrop().into(myUserProfile)
            }
                ?: glide.load(R.drawable.baseline_person_24).circleCrop().into(myUserProfile)
        }
    }
    inner class OtherChatItemViewHolder(private val binding: ItemOtherChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val storageReference = Firebase.storage.reference
        fun bind(uiState: ChatItemUiState) = with(binding) {
            textChatMessageOther.text = uiState.content
            otherUserDate.text = uiState.timestamp
            val glide = Glide.with(root)
            val ref = uiState.profileImg?.let { storageReference.child(it) }
            ref?.downloadUrl?.addOnSuccessListener { uri ->
                glide.load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fallback(R.drawable.baseline_person_24).circleCrop().into(otherUserProfile)
            }
                ?: glide.load(R.drawable.baseline_person_24).circleCrop().into(otherUserProfile)
        }
    }
}