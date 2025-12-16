package com.timeskip.ezlink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import com.timeskip.ezlink.features.link.linkGraph
import com.timeskip.ezlink.features.link.linkSearchGraph
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.tag.tagGraph

@Composable
fun AppNavHost(
    sharedTagName: String?,
    sharedUrl: String?,
    navController: NavHostController,
    resetSharedData: () -> Unit,
    modifier: Modifier
) {
    NavHost(navController, startDestination = TagNavigation) {
        tagGraph(sharedTagName, sharedUrl, navController, resetSharedData, modifier)
        linkGraph(navController, modifier)
        linkSearchGraph(navController, modifier)
    }

    LaunchedEffect(sharedTagName, sharedUrl) {
        if (sharedTagName != null && sharedUrl == null) {
            navController.navigateToLink(
                sharedTagName,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
    }
}
