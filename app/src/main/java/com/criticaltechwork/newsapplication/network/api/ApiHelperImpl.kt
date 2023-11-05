package com.criticaltechwork.newsapplication.network.api

import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.utils.Constants
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
  private val newsApi: APIServices
) : ApiHelper {
    override suspend fun getHeadlineNews(pageNumer: Int): Response<NewsResponse> {
        TODO("Not yet implemented")
        newsApi.getTopHeadlineNews(Constants.CountryCode, pageNumer)
    }

}