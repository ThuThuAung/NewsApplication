package com.criticaltechwork.newsapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.criticaltechwork.newsapplication.R
import com.criticaltechwork.newsapplication.databinding.NewsItemListBinding
import com.criticaltechwork.newsapplication.model.Article

class RvNewsAdapter : RecyclerView.Adapter<RvNewsAdapter.NewsAdapterViewHolder>() {

    inner class NewsAdapterViewHolder(val binding: NewsItemListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterViewHolder {
        val binding =
            NewsItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NewsAdapterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: NewsAdapterViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder) {
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.ivArticleImage)
            binding.tvPublishedAt.text = article.publishedAt
            binding.tvSource.text = article.source?.name
            binding.tvTitle.text = article.title
        }

        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}