package com.example.hansungmarket.control.signIn

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
import com.example.hansungmarket.databinding.ActivityLoginBinding
import com.example.hansungmarket.control.main.MainActivity
import com.example.hansungmarket.control.signUp.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SignInActivity::class.java)
        }
    }
    private val vm: SignInViewModel by viewModels()
    private lateinit var b: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)
        if (vm.login) mainView()
        initEv()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.uis.collect(::upd)
            }
        }
    }
    private fun initEv() = with(b) {
        email.addTextChangedListener {
            if (it != null) vm.updE(it.toString())
        }
        password.addTextChangedListener {
            if (it != null) vm.updP(it.toString())
        }
        signInButton.setOnClickListener {
            vm.login()
        }
        signUpText.setOnClickListener {
            signUpView()
        }
    }
    private fun upd(uiState: SignInUiState) {
        b.emailInputLayout.apply {
            isErrorEnabled = uiState.emailE
            error = if (uiState.emailE) {
                context.getString(R.string.noEmail)
            } else null
        }
        b.passwordInputLayout.apply {
            isErrorEnabled = uiState.passE
            error = if (uiState.passE) {
                context.getString(R.string.noP)
            } else null
        }
        if (uiState.isLogin) loginComp()
        if (uiState.errorMessage != null) {
            showSnackBar(uiState.errorMessage)
            vm.userMessageShown()
        }
        b.signInButton.apply {
            isEnabled = uiState.isInputValid && !uiState.L
            setText(if (uiState.L) R.string.loading else R.string.login)
        }
    }
    private fun loginComp() {
        vm.userInfoExists { exists ->
            if (exists) {
                val sharedPreferences = getSharedPreferences(getString(R.string.PF), Context.MODE_PRIVATE)
                sharedPreferences.edit().putBoolean(getString(R.string.PU), true).apply()
                mainView()
            }
        }
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(b.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun signUpView() {
        val intent = SignUpActivity.getIntent(this)
        startActivity(intent)
    }
    private fun mainView() {
        val intent = MainActivity.getIntent(this).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        startActivity(intent)
        finish()
    }
}