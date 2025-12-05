package com.timeskip.ezlink.features.link

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.timeskip.ezlink.features.link.search.LinkSearchContainer
import com.timeskip.ezlink.features.link.search.LinkSearchScreen
import com.timeskip.ezlink.navigation.LinkDestination
import com.timeskip.ezlink.navigation.LinkSearchNavigation

fun NavController.navigateToLink(tagName: String, navOptions: NavOptions? = null) {
    this.navigate(LinkDestination(tagName), navOptions = navOptions)
}

fun NavGraphBuilder.linkGraph(navController: NavController, modifier: Modifier) {
    composable<LinkDestination> {
        LinkScreenContainer(navController::popBackStack, modifier)
    }
}

fun NavController.navigateToSearchLink(search: String, navOptions: NavOptions? = null) {
    this.navigate(LinkSearchNavigation(search), navOptions = navOptions)
}

fun NavGraphBuilder.linkSearchGraph(navController: NavController, modifier: Modifier) {
    composable<LinkSearchNavigation> { navBackStackEntry ->
        val searchValue = navBackStackEntry.arguments?.getString("search") ?: ""
        LinkSearchContainer(searchValue, navController::popBackStack, modifier)
    }
}