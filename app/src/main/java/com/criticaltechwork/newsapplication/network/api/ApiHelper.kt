package com.criticaltechwork.newsapplication.network.api

import com.criticaltechwork.newsapplication.model.NewsResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun getHeadlineNews(country_code: String, pageNumer: Int) : Response<NewsResponse>
}