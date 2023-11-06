package com.criticaltechwork.newsapplication.ui.details

import android.app.Activity
import android.os.Build
import android.os.Bundle
import com.bumptech.glide.Glide
import com.criticaltechwork.newsapplication.R
import com.criticaltechwork.newsapplication.base.BaseActivity
import com.criticaltechwork.newsapplication.databinding.ActivityDetailsBinding
import com.criticaltechwork.newsapplication.model.Article
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailsBinding>() {

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        supportActionBar?.title = "Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val article = getSerializable(this, "news", Article::class.java)
        setupUI(article)

    }

    fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }

    override fun setBinding(): ActivityDetailsBinding = ActivityDetailsBinding.inflate(layoutInflater)

    fun setupUI(article: Article?) {
        if (article != null) {
            binding.tvTitle.text = article.title
            binding.tvContent.text = article.content
            binding.tvDescription.text = article.description
            binding.tvPublishedAt.text = article.publishedAt
            Glide.with(applicationContext)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.ivArticleImage)
        }

    }


}