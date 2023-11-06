package com.criticaltechwork.newsapplication.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.criticaltechwork.newsapplication.BuildConfig
import com.criticaltechwork.newsapplication.base.BaseActivity
import com.criticaltechwork.newsapplication.databinding.ActivityMainBinding
import com.criticaltechwork.newsapplication.model.Article
import com.criticaltechwork.newsapplication.ui.adapter.RvNewsAdapter
import com.criticaltechwork.newsapplication.ui.details.DetailActivity
import com.criticaltechwork.newsapplication.utils.Constants.QUERY_PER_PAGE
import com.criticaltechwork.newsapplication.utils.EndlessRecyclerOnScrollListener
import com.criticaltechwork.newsapplication.utils.EspressoIdlingResource
import com.criticaltechwork.newsapplication.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    val mainViewModel: MainViewModel by viewModels()
    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    private lateinit var rvNewsAdapter: RvNewsAdapter

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        supportActionBar?.title =  BuildConfig.COUNTRY_CODE.uppercase() + " Today's News"
        setupUI()
        setupRecyclerView()
        if (isBiometricSupported()) {
            showBiometricPrompt()
        } else {
            showMessage("Biometric Not Supported!")
            setupObservers()
        }
    }


    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun setupUI() {
        EspressoIdlingResource.increment()
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            mainViewModel.fetchNewsfromApi()
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                mainViewModel.fetchNewsfromApi()
            }
        }

        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.fetchNewsfromApi()
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                false
            }
            else -> {
                false
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showMessage("Authentication succeeded!")
                    setupObservers()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage("Authentication failed.")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun setupRecyclerView() {
        rvNewsAdapter = RvNewsAdapter()
        binding.rvNews.apply {
            adapter = rvNewsAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addOnScrollListener(onScrollListener)
        }
        rvNewsAdapter.setOnItemClickListener { news ->

            startActivity(launchNextScreen(applicationContext, news))

        }
    }

    private fun launchNextScreen(context: Context, news: Article): Intent {
        val bundle = Bundle().apply {
            putSerializable("news", news)
        }
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtras(bundle)
        return intent
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.newsResponse.collect { response ->
                when (response) {
                    is NetworkState.Success -> {
                        hideProgressBar()
                        hideErrorMessage()
                        response.data?.let { newResponse ->
                            EspressoIdlingResource.decrement()
                            rvNewsAdapter.differ.submitList(newResponse.articles.toList())
                            mainViewModel.totalPage =
                                newResponse.totalResults / QUERY_PER_PAGE + 1
                            onScrollListener.isLastPage =
                                mainViewModel.pageCount == mainViewModel.totalPage + 1
                            hideBottomPadding()
                        }
                    }

                    is NetworkState.Loading -> {
                        showProgressBar()
                    }

                    is NetworkState.Error -> {
                        hideProgressBar()
                        response.message?.let {
                            showErrorMessage(response.message)
                        }
                    }

                    else -> {}
                }
            }

        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.errorMessage.collect { value ->
                if (value.isNotEmpty()) {
                    Toast.makeText(applicationContext, value, Toast.LENGTH_LONG).show()
                }
                mainViewModel.hideErrorToast()
            }
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }

    private fun hideBottomPadding() {
        if (onScrollListener.isLastPage) {
            binding.rvNews.setPadding(0, 0, 0, 0)
        }
    }

}