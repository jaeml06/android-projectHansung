package com.example.hansungmarket.control.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.hansungmarket.R
import com.example.hansungmarket.databinding.ActivityMainBinding
import com.example.hansungmarket.control.main.fragment.sale.update.SaleAddActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navFrg =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navFrg.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_sale) {
                binding.fab.visibility = View.VISIBLE
                binding.appTitle.text = getString(R.string.appT)
            } else {
                binding.fab.visibility = View.INVISIBLE
                binding.appTitle.text = getString(R.string.app_name)
            }
        }
        binding.fab.setOnClickListener {
            navAdd()
        }
    }
    private fun navAdd() {
        startActivity(Intent(this, SaleAddActivity::class.java))
    }
}