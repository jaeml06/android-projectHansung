package com.example.hansungmarket.control.main.fragment.sale.update

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivitySaleAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class SaleAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaleAddBinding
    private val viewModel: SaleAddViewModel by viewModels()
    private val file =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            if (imageUri != null) {
                viewModel.S(imageUri)
            } else if (viewModel.uis.value.S == null && viewModel.uis.value.isC) {
                finish()
            }
        }
    companion object {
        fun getIntent(context: Context, id: String, c: String, i: String, t: String, cc: String): Intent {
            return Intent(context, SaleAddActivity::class.java).apply {
                putExtra("uuId", id)
                putExtra("title", t)
                putExtra("content", c)
                putExtra("cost", cc)
                putExtra("image", i)

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val t = intent.getStringExtra("title")
        val c = intent.getStringExtra("content")
        val i = intent.getStringExtra("image")
        val id = intent.getStringExtra("uuId")
        val cc = intent.getStringExtra("cost")
        val glide = Glide.with(this)
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://market-6c0a3.appspot.com/")
        val storageReference = storage.reference
        if (t != null && c != null && id != null && cc != null && i != null) {
            viewModel.toE()
            val postReference = i.let { storageReference.child(it) }
            postReference.downloadUrl.addOnSuccessListener { uri ->
                glide.load(uri).into(binding.imageView)
            }
            binding.toolbarTitle.text = getString(R.string.pe)
            binding.postButton.text = getString(R.string.peing)
            binding.content.setText(c)
            binding.title.setText(t)
            binding.cost.setText(cc)
        } else {
            showImgP()
        }
        binding.imageView.setOnClickListener {
            showImgP()
        }
        binding.title.addTextChangedListener {
            viewModel.updT(it.toString())
        }
        binding.content.addTextChangedListener {
            viewModel.updC(it.toString())
        }
        binding.cost.addTextChangedListener {
            viewModel.updCC(it.toString())
        }
        binding.postButton.setOnClickListener {
            if (!viewModel.uis.value.isC) {
                viewModel.eC(id!!)
            } else {
                viewModel.upS()
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect(::updUi)
            }
        }
    }
    private fun updUi(uis: SaleAddUiState) = with(binding) {
        if (uis.S != null)
            imageView.setImageURI(uis.S)
        if (uis.errorMessage != null)
            showSnackBar(getString(uis.errorMessage))
        titleInputLayout.apply {
            isErrorEnabled = uis.showTitleError
            error = if (uis.showTitleError) {
                context.getString(R.string.titleEmpty)
            } else null
        }

        contentInputLayout.apply {
            isErrorEnabled = uis.showContentError
            error = if (uis.showContentError) {
                context.getString(R.string.contentEmpty)
            } else null
        }

        costInputLayout.apply {
            isErrorEnabled = uis.showCostError
            error = if (uis.showCostError) {
                context.getString(R.string.notInt)
            } else null
        }
        if (uis.SU) {
            Toast.makeText(this@SaleAddActivity, "업로드 성공", Toast.LENGTH_LONG).show()
            setResult(RESULT_OK)
            finish()
        }
        binding.postButton.apply {
            isEnabled = uis.isInputValid && !uis.loading
            alpha = if (uis.loading) 0.5F else 1.0F
        }
    }
    private fun showImgP() {
        if (!viewModel.uis.value.loading) {
            file.launch("image/*")
        }
    }
    private fun showSnackBar(message: String) {
        val root = binding.postingRoot
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show()
    }
}