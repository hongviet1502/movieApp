package com.example.muv.di

import com.example.muv.ui.search.SearchHistoryManager
import com.example.muv.ui.search.SearchHistoryManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchHistoryManager(
        searchHistoryManagerImpl: SearchHistoryManagerImpl
    ): SearchHistoryManager
}