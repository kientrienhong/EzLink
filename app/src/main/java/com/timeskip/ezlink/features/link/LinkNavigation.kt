package com.timeskip.ezlink.features.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.timeskip.ezlink.navigation.LinkDestination

fun NavController.navigateToLink(tagName: String, navOptions: NavOptions? = null) {
    this.navigate(LinkDestination(tagName), navOptions = navOptions)
}

fun NavGraphBuilder.linkGraph(navController: NavController, modifier: Modifier) {
    composable<LinkDestination> {
        LinkScreen(navController::popBackStack, modifier)
    }
}
