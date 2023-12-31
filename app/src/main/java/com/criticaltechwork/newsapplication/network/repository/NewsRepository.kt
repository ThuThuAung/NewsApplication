package com.criticaltechwork.newsapplication.network.repository

import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.network.api.ApiHelper
import com.criticaltechwork.newsapplication.utils.NetworkState
import javax.inject.Inject

class NewsRepository @Inject constructor(
  private val remoteData : ApiHelper
) : INewsRespository {
    override suspend fun getHeadlineNews(country_code: String, pageCount: Int): NetworkState<NewsResponse> {
        return try {
            val response = remoteData.getHeadlineNews(country_code, pageCount)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                NetworkState.Success(result)
            } else {
                NetworkState.Error("An error occurred")
            }
        } catch (e: Exception) {
            NetworkState.Error("Error occurred ${e.localizedMessage}")
        }
    }

}