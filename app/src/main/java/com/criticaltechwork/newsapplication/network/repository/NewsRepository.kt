package com.criticaltechwork.newsapplication.network.repository

import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.network.api.ApiHelper
import com.criticaltechwork.newsapplication.utils.NetworkState
import retrofit2.Response
import javax.inject.Inject

class NewsRepository @Inject constructor(
  private val apiData : ApiHelper
) : INewsRespository{
    override suspend fun getHeadlineNews(pageCount: Int): NetworkState<NewsResponse> {
        TODO("Not yet implemented")

    }

}