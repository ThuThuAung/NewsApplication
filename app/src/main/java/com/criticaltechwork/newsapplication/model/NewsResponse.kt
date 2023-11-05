package com.criticaltechwork.newsapplication.model

data class NewResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)