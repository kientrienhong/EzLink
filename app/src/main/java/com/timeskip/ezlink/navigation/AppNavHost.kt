package com.timeskip.ezlink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.timeskip.ezlink.features.link.linkGraph
import com.timeskip.ezlink.features.tag.tagGraph

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = TagNavigation) {
        tagGraph(navController, modifier)
        linkGraph(navController, modifier)
    }
}
