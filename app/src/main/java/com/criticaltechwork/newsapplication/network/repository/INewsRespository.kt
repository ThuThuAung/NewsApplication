package com.criticaltechwork.newsapplication.network.repository

import com.criticaltechwork.newsapplication.model.NewsResponse
import com.criticaltechwork.newsapplication.utils.NetworkState

interface INewsRespository {
    suspend fun getHeadlineNews(country_code: String, pageCount: Int) : NetworkState<NewsResponse>
}