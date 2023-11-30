package com.example.hansungmarket.control.main.fragment.profile.profileupdate

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivityProfileUpdateBinding
import com.example.hansungmarket.model.UserDetail
import com.example.hansungmarket.control.utils.getSerializable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class ProfileUpdateActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context, userDetail: UserDetail): Intent {
            return Intent(context, ProfileUpdateActivity::class.java).apply {
                putExtra("userDetail", userDetail)
            }
        }
    }
    private lateinit var binding: ActivityProfileUpdateBinding
    private val viewModel: ProfileUpdateViewModel by viewModels()
    private fun Uri.toBitmap(context: Context): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(context.contentResolver, this)
            ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION") BitmapDrawable(
                context.resources, MediaStore.Images.Media.getBitmap(context.contentResolver, this)
            ).bitmap
        }
    private val P =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri ->
            if (imageUri != null) {
                val bitmap = imageUri.toBitmap(this)
                viewModel.updI(bitmap)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val D = intent.getSerializable("userDetail", UserDetail::class.java)
        viewModel.bind(D.name)
        initUi(D)
        init()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect(::updUi)
            }
        }
    }
    private fun init() = with(binding) {
        backButton.setOnClickListener {
            finish()
        }
        doneButton.setOnClickListener {
            viewModel.sC()
        }
    }
    private fun initUi(userDetail: UserDetail) {
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://market-6c0a3.appspot.com/")
        val storageReference = storage.reference
        val pathReference = userDetail.profileImgUrl?.let { storageReference.child(it) }
        binding.apply {
            imageView.setOnClickListener { clickImg(userDetail) }
            pathReference?.downloadUrl?.addOnSuccessListener { uri ->
                Glide.with(this@ProfileUpdateActivity).load(uri)
                    .fallback(R.drawable.baseline_person_24).circleCrop().into(binding.profileImage)
            }
            userNameEditText.setText(userDetail.name)
            userNameEditText.addTextChangedListener {
                if (it != null) viewModel.updN(it.toString())
            }
        }
    }
    private fun updImg(bitmap: Bitmap?) {
        Glide.with(this@ProfileUpdateActivity).load(bitmap).fallback(R.drawable.baseline_person_24)
            .circleCrop().into(binding.profileImage)
    }
    private fun updUi(uiState: ProfileUpdateUiState) {
        binding.doneButton.apply {
            val canSave = viewModel.saved
            isEnabled = canSave
        }
        if (uiState.isc) updImg(uiState.sib)
        if (uiState.suc) {
            showSnackBar(getString(R.string.upd_prof))
            finish()
        }
        if (uiState.errorMessage != null) {
            showSnackBar(uiState.errorMessage)
            viewModel.errorMessageShown()
        }
    }
    private fun clickImg(userDetail: UserDetail) {
        val cur = viewModel.uis.value.sib
        val prv = userDetail.profileImgUrl
        val isc = viewModel.uis.value.isc
        if (cur == null && (prv == null || isc)) {
            imgP()
        } else {
            MaterialAlertDialogBuilder(this).setItems(R.array.imgOption) { _, which ->
                when (which) {
                    0 -> imgP()
                    1 -> viewModel.updI(null)
                    else -> throw IllegalArgumentException()
                }
            }.create().show()
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun imgP() {
        P.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}