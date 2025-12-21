package com.timeskip.ezlink.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import com.timeskip.ezlink.features.link.linkGraph
import com.timeskip.ezlink.features.link.linkSearchGraph
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.tag.popUpToTagListOrNavigate
import com.timeskip.ezlink.features.tag.tagGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    sharedUrl: String?,
    sharedTagName: String?,
    onConsumeSharedIntent: () -> Unit,
) {
    LaunchedEffect(sharedTagName, sharedUrl) {
        if (sharedTagName != null && sharedUrl == null) {
            navController.navigateToLink(
                sharedTagName,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
            onConsumeSharedIntent()
        } else if (sharedUrl != null) {
            navController.popUpToTagListOrNavigate(
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
    }


    NavHost(navController, startDestination = TagNavigation) {
        tagGraph(
            navController = navController,
            modifier = modifier,
            sharedUrl = sharedUrl,
            sharedTagName = sharedTagName,
            onConsumeSharedIntent = onConsumeSharedIntent,
        )
        linkGraph(navController, modifier)
        linkSearchGraph(navController, modifier)
    }
}
