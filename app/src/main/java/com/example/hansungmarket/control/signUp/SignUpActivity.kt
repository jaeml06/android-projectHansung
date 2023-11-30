package com.example.hansungmarket.control.signUp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SignUpActivity::class.java)
        }
    }
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initEL()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uis.collect(::updUi)
            }
        }
    }
    private fun initEL() = with(binding) {
        email.addTextChangedListener {
            if (it != null) viewModel.updE(it.toString())
        }
        password.addTextChangedListener {
            if (it != null) {
                viewModel.updP(it.toString())
            }
        }
        confirmPassword.addTextChangedListener {
            if (it != null) {
                viewModel.updCP(it.toString())
            }
        }
        binding.signUpButton.setOnClickListener {
            viewModel.login()
        }
    }
    private fun updUi(uis: SignUpUiState) = with(binding) {
        emailInputLayout.apply {
            isErrorEnabled = uis.showEmailError
            error = if (uis.showEmailError) {
                context.getString(R.string.noEmail)
            } else null
        }
        passwordInputLayout.apply {
            isErrorEnabled = uis.showPasswordError
            error = if (uis.showPasswordError) {
                context.getString(R.string.noP)
            } else null
        }
        confirmPasswordInputLayout.apply {
            isErrorEnabled = uis.showConfirmPasswordError
            error = if (uis.showConfirmPasswordError) {
                context.getString(R.string.wrongP)
            } else null
        }
        if (uis.S) navInfo()
        if (uis.errorMessage != null) {
            showSnackBar(uis.errorMessage)
            viewModel.userMessageShown()
        }
        signUpButton.apply {
            isEnabled = uis.valid && !uis.loading
            setText(if (uis.loading) R.string.loading else R.string.reg)
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(this, binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun navInfo() {
        val intent = UserInfoActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}