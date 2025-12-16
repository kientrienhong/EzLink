package com.timeskip.ezlink.features.di

import android.content.Context
import com.timeskip.ezlink.features.common.TagShortcutManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {
    @Provides
    fun provideTagShortcutManager(
        @ApplicationContext context: Context
    ): TagShortcutManager = TagShortcutManager(context)
}