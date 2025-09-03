package com.example.linkkeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.linkkeeper.features.link.linkGraph
import com.example.linkkeeper.features.splash.splashGraph
import com.example.linkkeeper.features.tag.tagGraph

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = SplashDestination) {
        splashGraph(navController)
        tagGraph(navController, modifier)
        linkGraph(navController, modifier)
    }
}
