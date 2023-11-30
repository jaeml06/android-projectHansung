package com.example.hansungmarket.control.main.fragment.profile

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hansungmarket.control.main.fragment.profile.profileupdate.ProfileUpdateActivity
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.FragmentProfileBinding
import com.example.hansungmarket.model.UserDetail
import com.example.hansungmarket.control.utils.PagingLoadStateAdapter
import com.example.hansungmarket.control.utils.ViewBindingFragment
import com.example.hansungmarket.control.utils.registerObserverForScrollToTop
import com.example.hansungmarket.control.signIn.SignInActivity
import com.example.hansungmarket.control.main.fragment.sale.SaleAdapter
import com.example.hansungmarket.control.main.fragment.sale.SaleItemUiState
import com.example.hansungmarket.control.main.fragment.sale.detail.SaleDetailActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class ProfileFragment : ViewBindingFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileViewModel by activityViewModels()
    override val bf: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate
    private lateinit var launcher: ActivityResultLauncher<Intent>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bind()
        val adapter = SaleAdapter(onClickSaleItem = ::clickI)
        init()
        initR(adapter)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.bind()
                adapter.refresh()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect {
                    updUi(it, adapter)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.bind()
    }
    private fun init() = with(b) {
        profileUpdateButton.setOnClickListener { navToUpd(viewModel.uis.value.det!!) }
        logoutButton.setOnClickListener { logOut() }
    }
    private fun initR(adapter: SaleAdapter) = with(b) {
        recyclerView.adapter = adapter.withLoadStateFooter(PagingLoadStateAdapter { adapter.retry() })
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.registerObserverForScrollToTop(recyclerView)
    }

    private fun updUi(uiState: ProfileUiState, adapter: SaleAdapter) = with(b) {
        adapter.submitData(viewLifecycleOwner.lifecycle, uiState.posts)
        val storage: FirebaseStorage = FirebaseStorage.getInstance("gs://market-6c0a3.appspot.com/")
        val storageReference = storage.reference
        if (uiState.errorMessage != null) {
            viewModel.errorMessageShown()
            showSnackBar(uiState.errorMessage)
        }
        if (uiState.det != null) {
            val glide = Glide.with(root)
            if (uiState.det.profileImgUrl != null) {
                val ref =
                    uiState.det.profileImgUrl.let { storageReference.child(it) }
                ref.downloadUrl.addOnSuccessListener { uri ->
                    glide.load(uri).fallback(R.drawable.baseline_person_24).circleCrop()
                        .into(userProfileImage)
                }
            } else {
                glide.load(R.drawable.baseline_person_24).circleCrop().into(userProfileImage)
            }
            userNameText.text = uiState.det.name
        }
    }
    private fun logOut() {
        Firebase.auth.signOut()
        requireActivity().finish()
        val intent = SignInActivity.getIntent(requireContext())
        startActivity(intent)
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(b.root, message, Snackbar.LENGTH_SHORT).show()
    }
    private fun navToUpd(userDetail: UserDetail) {
        val intent = ProfileUpdateActivity.getIntent(requireContext(), userDetail)
        launcher.launch(intent)
    }
    private fun clickI(saleItemUiState: SaleItemUiState) {
        val intent = SaleDetailActivity.getIntent(requireContext(), saleItemUiState.id)
        launcher.launch(intent)
    }
}