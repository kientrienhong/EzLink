package com.example.linkkeeper.features.di

import com.example.linkkeeper.features.contentHtml.ContentHtmlRepository
import com.example.linkkeeper.features.contentHtml.ContentHtmlRepositoryImpl
import com.example.linkkeeper.features.link.data.LinkRepository
import com.example.linkkeeper.features.link.data.LinkRepositoryImpl
import com.example.linkkeeper.features.tag.data.TagRepository
import com.example.linkkeeper.features.tag.data.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideTagRepository(tagRepositoryImpl: TagRepositoryImpl): TagRepository

    @Binds
    fun provideLinkRepository(linkRepositoryImpl: LinkRepositoryImpl): LinkRepository

    @Binds
    fun provideContentHtmlRepository(
        contentHtmlRepositoryImpl: ContentHtmlRepositoryImpl
    ): ContentHtmlRepository
}