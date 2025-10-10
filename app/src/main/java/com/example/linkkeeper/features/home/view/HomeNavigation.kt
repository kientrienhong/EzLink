package com.example.linkkeeper.features.home.view

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.linkkeeper.features.link.navigateToLink
import com.example.linkkeeper.features.searchlink.navigateToSearch
import com.example.linkkeeper.navigation.HomeNavigation

fun NavController.navigateToHomeTag(navOptions: NavOptions? = null) {
    this.navigate(route = HomeNavigation, navOptions = navOptions)
}

fun NavGraphBuilder.homeGraph(navController: NavController, modifier: Modifier = Modifier) {
    composable<HomeNavigation> {
        HomeScreen(modifier, onSearchClick = { navController.navigateToSearch() }) {
            navController.navigateToLink(it.id, it.name)
        }
    }
}