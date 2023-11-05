package com.criticaltechwork.newsapplication.di

import com.criticaltechwork.newsapplication.network.api.ApiHelper
import com.criticaltechwork.newsapplication.network.repository.INewsRespository
import com.criticaltechwork.newsapplication.network.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(
        remoteData: ApiHelper
    ) = NewsRepository(remoteData)

    @Singleton
    @Provides
    fun provideINewsRepository(repository: NewsRepository): INewsRespository = repository
}