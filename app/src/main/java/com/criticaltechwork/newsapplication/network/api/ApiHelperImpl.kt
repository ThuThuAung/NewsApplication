package com.criticaltechwork.newsapplication.network.api

import com.criticaltechwork.newsapplication.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(
  private val newsApi: APIServices
) : ApiHelper {
    override suspend fun getHeadlineNews(country_code: String, pageNumer: Int): Response<NewsResponse> =
        newsApi.getTopHeadlineNews(country_code, pageNumer)

}