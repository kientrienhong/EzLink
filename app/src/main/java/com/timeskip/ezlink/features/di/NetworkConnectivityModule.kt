package com.timeskip.ezlink.features.di

import com.timeskip.ezlink.features.common.ConnectivityObserver
import com.timeskip.ezlink.features.common.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkConnectivityModule {

    @Singleton
    @Binds
    fun provideNetworkConnectivityObserver(
        networkConnectivityObserver: NetworkConnectivityObserver
    ): ConnectivityObserver
}