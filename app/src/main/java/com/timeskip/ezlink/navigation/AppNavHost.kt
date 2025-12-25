package com.timeskip.ezlink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import com.timeskip.ezlink.ShareInfoModel
import com.timeskip.ezlink.features.link.linkGraph
import com.timeskip.ezlink.features.link.linkSearchGraph
import com.timeskip.ezlink.features.link.navigateToLink
import com.timeskip.ezlink.features.tag.popUpToTagListOrNavigate
import com.timeskip.ezlink.features.tag.tagGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    shareInfoModel: ShareInfoModel?,
    modifier: Modifier,
    onConsumeSharedIntent: () -> Unit,
) {
    LaunchedEffect(shareInfoModel) {
        val sharedUrl = shareInfoModel?.sharedUrl
        val sharedTagName = shareInfoModel?.sharedTagName
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
            shareInfoModel = shareInfoModel,
            onConsumeSharedIntent = onConsumeSharedIntent,
        )
        linkGraph(navController, modifier)
        linkSearchGraph(navController, modifier)
    }
}
