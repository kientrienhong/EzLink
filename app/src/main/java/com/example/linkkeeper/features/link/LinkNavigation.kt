package com.example.linkkeeper.features.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.linkkeeper.features.link.list.LinkListScreen
import com.example.linkkeeper.navigation.LinkDestination

fun NavController.navigateToLink(tagId: String, tagName: String?, navOptions: NavOptions? = null) {
    this.navigate(LinkDestination(tagId, tagName), navOptions = navOptions)
}

fun NavGraphBuilder.linkGraph(navController: NavController, modifier: Modifier) {
    composable<LinkDestination> {
        LinkListScreen(modifier, navController::popBackStack)
    }
}
