package com.example.hansungmarket.control.main.fragment.sale.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivitySaleDetailBinding
import com.example.hansungmarket.control.main.fragment.sale.update.SaleAddActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch


class SaleDetailActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, postId: String): Intent {
            return Intent(context, SaleDetailActivity::class.java).apply {
                putExtra("postUuId", postId)
            }
        }
    }
    private val viewModel: SaleDetailViewModel by viewModels()
    private lateinit var binding: ActivitySaleDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initE()
        val postId = intent.getStringExtra("postUuId")
        viewModel.bind(postUuId = requireNotNull(postId))
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect(::updUi)
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val postId = intent.getStringExtra("postUuId")
        viewModel.bind(postUuId = requireNotNull(postId))
    }
    private fun initE() = with(binding) {
        backButton.setOnClickListener {
            finish()
        }
        chattingRoomButton.setOnClickListener {
            viewModel.chat()
        }
        menuButton.setOnClickListener { view ->
            val menu = PopupMenu(applicationContext, view)
            menuInflater.inflate(R.menu.post_detail_menu, menu.menu)
            menu.show()
            menu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.update -> {
                        onClickUpdatePostMenu(viewModel.uis.value)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.delete -> {
                        onClickDeletePostMenu(viewModel.uis.value)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.salePossible -> {
                        onClickSalePossibleUpdateMenu(viewModel.uis.value)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun updUi(uiState: SaleDetailUiState) = with(binding) {
        val det = uiState.selectI
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://project-android-fde93.appspot.com/")
        val storageReference = storage.reference
        if (uiState.errorMessage != null) {
            showSnackBar(uiState.errorMessage)
            viewModel.errorMessageShown()
        }
        if (uiState.delEnd) finish()
        if (det != null) {
            val glide = Glide.with(root)
            val ref = det.imgUrl.let { storageReference.child(it) }
            ref.downloadUrl.addOnSuccessListener { uri ->
                glide.load(uri).into(contentImage)
            }
            if (det.sImgUrl != null) {
                val profileReference = det.sImgUrl.let { storageReference.child(it) }
                profileReference.downloadUrl.addOnSuccessListener { uri ->
                    glide.load(uri).fallback(R.drawable.baseline_person_24).circleCrop()
                        .into(profileImage)
                }
            } else {
                glide.load(R.drawable.baseline_person_24).circleCrop().into(profileImage)
            }
            nameText.text = det.sName
            titleText.text = det.T
            content.text = det.content
            daysText.text = det.timestamp
            costText.text = det.C
            requireNotNull(uiState.canBuy)
            if (uiState.canBuy) {
                salesCheckingText.text = root.context.getString(R.string.doing)
                salesCheckingText.setBackgroundColor(getColor(R.color.md_theme_light_primaryContainer))
            } else {
                salesCheckingText.text = root.context.getString(R.string.done)
                salesCheckingText.setBackgroundColor(getColor(R.color.md_theme_dark_primaryContainer))
            }
            menuButton.isVisible = det.M
            chattingRoomButton.isVisible = !det.M
        }
    }

    private fun onClickDeletePostMenu(uiState: SaleDetailUiState) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.del))
            setMessage(R.string.check)
            setNegativeButton(R.string.no) { _, _ -> }
            setPositiveButton(R.string.yesd) { _, _ -> viewModel.delSelectP(uiState) }
        }.show()
    }

    private fun onClickUpdatePostMenu(uiState: SaleDetailUiState) {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.upd))
            setMessage(R.string.checke)
            setNegativeButton(R.string.no) { _, _ -> }
            setPositiveButton(R.string.yese) { _, _ -> navigateToEditActivity(uiState) }
        }.show()
    }

    private fun onClickSalePossibleUpdateMenu(uiState: SaleDetailUiState) {
        requireNotNull(uiState.canBuy)
        if (uiState.canBuy) {
            MaterialAlertDialogBuilder(this).apply {
                setTitle(getString(R.string.cngState))
                setMessage(R.string.checkComp)
                setNegativeButton(R.string.no) { _, _ -> }
                setPositiveButton(R.string.yesc) { _, _ ->
                    viewModel.canSaleEdit(uiState.selectI!!.id, uiState.canBuy)
                }
            }.show()
        } else {
            MaterialAlertDialogBuilder(this).apply {
                setTitle(getString(R.string.cngState))
                setMessage(R.string.checkComp_)
                setNegativeButton(R.string.no) { _, _ -> }
                setPositiveButton(R.string.yesc) { _, _ ->
                    viewModel.canSaleEdit(uiState.selectI!!.id, uiState.canBuy)
                }
            }.show()
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    private fun navigateToEditActivity(uiState: SaleDetailUiState) {
        val id = uiState.selectI!!.id
        val T = uiState.selectI.T
        val C = uiState.selectI.content
        val I = uiState.selectI.imgUrl
        val CC = uiState.selectI.C
        val intent = SaleAddActivity.getIntent(this@SaleDetailActivity, id = id, t = T, c = C, cc = CC, i = I)
        startActivity(intent)
    }
}
