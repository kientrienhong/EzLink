package com.example.linkkeeper.features.tag

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.linkkeeper.features.link.navigateToLink
import com.example.linkkeeper.features.tag.view.TagScreen
import com.example.linkkeeper.navigation.TagListDestination
import com.example.linkkeeper.navigation.TagNavigation


fun NavGraphBuilder.tagGraph(navController: NavController, modifier: Modifier) {
    navigation<TagNavigation>(TagListDestination) {
        composable<TagListDestination> {
            TagScreen(modifier) { id, name -> navController.navigateToLink(id, name) }
        }
    }
}