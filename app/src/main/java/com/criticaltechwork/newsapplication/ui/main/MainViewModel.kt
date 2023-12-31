package com.criticaltechwork.newsapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.criticaltechwork.newsapplication.BuildConfig
import com.criticaltechwork.newsapplication.di.CoroutinesDispatcherProvider
import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.network.repository.NewsRepository
import com.criticaltechwork.newsapplication.utils.NetworkHelper
import com.criticaltechwork.newsapplication.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _newsResponse = MutableStateFlow<NetworkState<NewsResponse>>(NetworkState.Empty())
    val newsResponse: StateFlow<NetworkState<NewsResponse>>
        get() = _newsResponse
    var pageCount = 1;
    var totalPage = 1;
    private var feedResponse: NewsResponse? = null
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String>
        get() = _errorMessage

    init {
        fetchNewsfromApi()
    }

    fun fetchNewsfromApi() {
        if(networkHelper.isNetworkConnected()) {
            viewModelScope.launch(coroutinesDispatcherProvider.io) {
                _newsResponse.value = NetworkState.Loading()
                when( val response = repository.getHeadlineNews(BuildConfig.COUNTRY_CODE, pageCount)) {
                    is NetworkState.Success -> {
                        _newsResponse.value = handleNewsResponse(response)
                    }
                    is NetworkState.Error -> {
                        _newsResponse.value =
                            NetworkState.Error(
                                response.message ?: "Error"
                            )
                    }
                    else -> {}
                }
            }
        } else {
            _errorMessage.value = "No internet available"
        }

    }

    private fun handleNewsResponse(response: NetworkState<NewsResponse>): NetworkState<NewsResponse> {
        response.data?.let { resultResponse ->
            if (feedResponse == null) {
                pageCount = 2
                feedResponse = resultResponse
            } else {
                pageCount++
                val oldArticles = feedResponse?.articles
                val newArticles = resultResponse.articles
                oldArticles?.addAll(newArticles)
            }

            return NetworkState.Success(feedResponse ?: resultResponse)
        }
        return NetworkState.Error("No data found")
    }

    fun hideErrorToast() {
        _errorMessage.value = ""
    }

}