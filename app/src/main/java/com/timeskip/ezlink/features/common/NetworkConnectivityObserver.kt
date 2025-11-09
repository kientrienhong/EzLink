package com.timeskip.ezlink.features.common

import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context
) : ConnectivityObserver {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observer(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    trySend(ConnectivityObserver.Status.Available)
                }

                override fun onUnavailable() {
                    trySend(ConnectivityObserver.Status.Unavailable)
                }

                override fun onLosing(network: android.net.Network, maxMsToLive: Int) {
                    trySend(ConnectivityObserver.Status.Losing)
                }

                override fun onLost(network: android.net.Network) {
                    trySend(ConnectivityObserver.Status.Lost)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }
}