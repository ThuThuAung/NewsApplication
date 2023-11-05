package com.criticaltechwork.newsapplication.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.criticaltechwork.newsapplication.di.CoroutinesDispatcherProvider
import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.network.repository.NewsRepository
import com.criticaltechwork.newsapplication.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _newsResponse = MutableStateFlow<NetworkState<NewsResponse>>(NetworkState.Empty())
    var pageCount = 1;
    private var feedResponse: NewsResponse? = null
    private val _errorMessage = MutableStateFlow("")

    init {
        fetchNewsfromApi();
    }

    fun fetchNewsfromApi() {
            viewModelScope.launch(coroutinesDispatcherProvider.io) {
                _newsResponse.value = NetworkState.Loading()
                when( val response = repository.getHeadlineNews(pageCount)) {
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