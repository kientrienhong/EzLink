package com.timeskip.ezlink.features.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object ConnectivityUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Get the active network
        val network = connectivityManager.activeNetwork ?: return false


        // Get the capabilities of that network
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        // Check if the network has a validated internet connection
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}