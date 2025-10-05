package com.example.linkkeeper.features.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.linkkeeper.navigation.SearchNavigation


fun NavController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(route = SearchNavigation, navOptions = navOptions)
}

fun NavGraphBuilder.searchGraph(navController: NavController, modifier: Modifier = Modifier) {
    composable<SearchNavigation> {
        SearchLinkScreen(modifier.fillMaxSize())
    }
}