package com.timeskip.ezlink.features.di

import com.timeskip.ezlink.features.contentHtml.ContentHtmlRepository
import com.timeskip.ezlink.features.contentHtml.ContentHtmlRepositoryImpl
import com.timeskip.ezlink.features.link.data.LinkRepository
import com.timeskip.ezlink.features.link.data.LinkRepositoryImpl
import com.timeskip.ezlink.features.tag.data.TagRepository
import com.timeskip.ezlink.features.tag.data.TagRepositoryImpl
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