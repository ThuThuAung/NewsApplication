package com.criticaltechwork.newsapplication.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.criticaltechwork.newsapplication.R
import com.criticaltechwork.newsapplication.base.BaseActivity
import com.criticaltechwork.newsapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val mainViewModel: MainViewModel by viewModels()
    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        supportActionBar?.title = "Today's News"

        savedInstanceState?.let {
        }
        setupUI()
    }

    fun setupUI() {
        binding.apply {
            tvText.text = "Today News List"
        }
    }

    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
}