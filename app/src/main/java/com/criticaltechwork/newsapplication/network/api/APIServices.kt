package com.criticaltechwork.newsapplication.network.api

import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.utils.Constants.API_KEY
import com.criticaltechwork.newsapplication.utils.Constants.CountryCode
import com.criticaltechwork.newsapplication.utils.Constants.QUERY_PER_PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIServices {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlineNews(
        @Query("country")
        countryCode: String = CountryCode,
        @Query("page")
        pageNumber: Int = 1,
        @Query("pageSize")
        pageSize: Int = QUERY_PER_PAGE,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResponse>
}