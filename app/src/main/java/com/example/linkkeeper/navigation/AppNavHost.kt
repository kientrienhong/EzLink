package com.example.linkkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.linkkeeper.features.home.view.homeGraph
import com.example.linkkeeper.features.link.detail.detailLinkGraph
import com.example.linkkeeper.features.link.linkGraph
import com.example.linkkeeper.features.searchlink.searchGraph
import com.example.linkkeeper.features.tag.tagGraph

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = HomeNavigation) {
        homeGraph(navController, modifier)
        tagGraph(navController, modifier)
        linkGraph(navController, modifier)
        searchGraph(navController, modifier)
        detailLinkGraph(navController, modifier)
    }
}
