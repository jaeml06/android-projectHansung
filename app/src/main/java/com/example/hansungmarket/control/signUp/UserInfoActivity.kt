package com.example.hansungmarket.control.signUp


import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivityUserInfoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class UserInfoActivity : AppCompatActivity() {
    private val viewModel: UserInfoViewModel by viewModels()
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UserInfoActivity::class.java)
        }
    }
    private val pick =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            if (imageUri != null) {
                @Suppress("DEPRECATION")
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            contentResolver,
                            imageUri
                        )
                    )
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                viewModel.S = bitmap
                binding.profileImage.setImageBitmap(bitmap)
            }
        }
    private lateinit var binding: ActivityUserInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.userNameEditText.setText(viewModel.N)
        viewModel.S?.let { binding.profileImage.setImageBitmap(it) }
        binding.profileImage.setOnClickListener {
            onClickImage()
        }
        binding.userNameEditText.addTextChangedListener {
            if (it != null) {
                viewModel.N = it.toString()
                updD()
            }
        }
        binding.doneButton.setOnClickListener {
            viewModel.sendInfo()
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect(::updUi)
            }
        }
    }
    private fun updUi(uiState: UserInfoUiState) {
        updD()
        when (uiState) {
            UserInfoUiState.E -> {
                val sharedPreferences = getSharedPreferences(
                    getString(R.string.PF),
                    Context.MODE_PRIVATE
                )
                sharedPreferences.edit()
                    .putBoolean(getString(R.string.PU), true)
                    .apply()
                navW()
            }
            is UserInfoUiState.F -> {
                showSnackBar(getString(R.string.F))
            }
            else -> {}
        }
    }

    private fun updD() {
        val isLoading = viewModel.uis.value is UserInfoUiState.Loading
        val hasName = binding.userNameEditText.text.toString().isNotEmpty()
        binding.doneButton.apply {
            isEnabled = hasName && !isLoading
            text = getString(if (isLoading) R.string.loading else R.string.start)
        }
    }
    private fun onClickImage() {
        if (viewModel.S != null) {
            MaterialAlertDialogBuilder(this)
                .setItems(R.array.imgOption) { _, which ->
                    when (which) {
                        0 -> showImagePicker()
                        1 -> {
                            viewModel.S = null
                            binding.profileImage.setImageDrawable(
                                AppCompatResources.getDrawable(
                                    this,
                                    R.drawable.baseline_person_24
                                )
                            )
                        }
                        else -> throw IllegalArgumentException()
                    }
                }.create()
                .show()
        } else {
            showImagePicker()
        }
    }
    private fun showImagePicker() {
        pick.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun navW() {
        val intent = WelcomeActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }
}
