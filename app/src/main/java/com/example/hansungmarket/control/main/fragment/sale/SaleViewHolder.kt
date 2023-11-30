package com.example.hansungmarket.control.main.fragment.sale

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ItemSalesBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SaleViewHolder(
    private val binding: ItemSalesBinding, private val onClickSaleItem: (SaleItemUiState) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val storageReference = Firebase.storage.reference
    fun bind(uiState: SaleItemUiState) = with(binding) {
        val glide = Glide.with(root)
        val sRef = uiState.sImgUrl?.let { storageReference.child(it) }
        val pRef = uiState.imgUrl.let { storageReference.child(it) }
        salesTitleText.text = uiState.T
        nickNameText.text = uiState.sName
        if (uiState.canSale) {
            salesCheckingText.text = root.context.getString(R.string.doing)
            salesCheckingText.setBackgroundColor(root.context.getColor(R.color.md_theme_light_primaryContainer))
        } else {
            salesCheckingText.text = root.context.getString(R.string.done)
            salesCheckingText.setBackgroundColor(root.context.getColor(R.color.md_theme_dark_primaryContainer))
        }
        if (sRef != null) {
            sRef.downloadUrl.addOnSuccessListener { uri ->
                glide.load(uri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fallback(R.drawable.baseline_person_24).circleCrop().into(userProfileImage)
            }
        } else {
            glide.load(R.drawable.baseline_person_24).circleCrop().into(userProfileImage)
        }
        pRef.downloadUrl.addOnSuccessListener { uri ->
            glide.load(uri).into(imageView)
        }
        salesCost.text = uiState.C
        beforeDayText.text = uiState.timestamp
        root.setOnClickListener { onClickSaleItem(uiState) }
    }
}
